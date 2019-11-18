package edu.knoldus

import java.io.File
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date

import edu.knoldus.hdfsutils.ConnectionProvider
import edu.knoldus.model._
import edu.knoldus.producer.DataProducer
import edu.knoldus.utility.{DataGenerator, FileUtility}
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

object BombardierData extends App {
  implicit val formats: DefaultFormats.type = DefaultFormats
  val lambda = (x: Int, y: Int) => (x + y, x - y, x * y, x / y)
  val uniqueObjects = List("Person","Car", "Building", "Bus", "Pole", "Bag", "Drone", "Truck", "Person", "Person")
  println(s"========================= ${FutureHelper.fileList}")
 val imagesPerCamera: List[File] = FutureHelper.fileList.zipWithIndex.flatMap{
   case (element, index)=>
     println(s"============== statring for index $index")
     (1 to 50).map(_ => element)
 }
/*   val imagesPerCamera: List[Int] = (1 to 10).toList.zipWithIndex.flatMap{
     case (element, index)=>
       println(s"============== statring for index $index")
       (1 to 100).map(_ => element)
   }*/
  val cameraId = "ASD$1231241"
//  val cameraIds = (1 to 10).map(count => s"$cameraId-$count")
//   val unitIds = (1 to 20).toList.map(_ => "77e5afa2-d882-11e9-994a-00044be64e82")
//    val unitIds = List("a68e0614-f2d2-11e9-8d6b-00044be6503a")
  val unitIds = List(java.util.UUID.randomUUID.toString)
// val unitIds = (1 to 10).toList.map(_ => java.util.UUID.randomUUID.toString)
  val imageHeaderData = DataGenerator.getDataToPublish.imageHeaderData.head
  val gpsData = DataGenerator.getDataToPublish.gpsData.head
  val imuData = DataGenerator.getDataToPublish.imuData.head

  println(s"GPS ${DataGenerator.getDataToPublish.gpsData.length}\n =IMU ${DataGenerator.getDataToPublish.imuData.length}\nImageHeader = ${DataGenerator.getDataToPublish.imageHeaderData.length}")

  def publishImageHeader: Future[List[(ObjectDataMessage, String)]] = Future {
    val objectDetector = java.util.UUID.randomUUID.toString
    unitIds.flatMap (unitId => {
      println(s"publishing for $unitId")
      val imageId = java.util.UUID.randomUUID().toString
      imagesPerCamera.zipWithIndex.map { case (file, index: Int) =>
         val byteArray = Files.readAllBytes(file.toPath) //excess overhead
        DataProducer.writeToKafka(ConfigConstants.imageHeaderTopic, unitId, write(imageHeaderData.copy(timestamp = System.currentTimeMillis(), imageId = f"$imageId-$index%05d", unitId = unitId, imageCounter = index)))
         DataProducer.writeToKafka(ConfigConstants.imageTopic, f"${unitId}_$imageId-$index%05d-L.jpg", byteArray)
         DataProducer.writeToKafka(ConfigConstants.imageTopic, f"${unitId}_$imageId-$index%05d-R.jpg", byteArray)
//        DataProducer.writeToKafka(ConfigConstants.imageTopic, f"${unitId}_$imageId-$index%05d-L.jpg", WebCamTester.getImage)
  //      DataProducer.writeToKafka(ConfigConstants.imageTopic, f"${unitId}_$imageId-$index%05d-R.jpg", WebCamTester.getImage)
        Thread.sleep(100)
        publishImageObjects(unitId, f"$imageId-$index%05d", imageId, objectDetector, index)
      }
    })
  }

  def publishGPSData = Future {
    unitIds foreach { camera =>
      imagesPerCamera.foreach { _ =>
        DataProducer.writeToKafka(ConfigConstants.imageGPSTopicSubscribe, camera,
          write(gpsData.copy(timestampLinux = System.currentTimeMillis(),
         latitude = Coordinates((System.currentTimeMillis() % 360).toInt, System.currentTimeMillis() % 60), latitudeNS = if(System.currentTimeMillis() % 2 == 0) "N" else "S",
         longitude = Coordinates((System.currentTimeMillis() % 360).toInt, System.currentTimeMillis() % 60), longitudeEW = if(System.currentTimeMillis() % 2 == 0) "E" else "W",
          unitId = camera,
          timestampGPS = FileUtility.GPS_DATE_FORMATTER.format(Instant.now()))))
        Thread.sleep(100)
      }
    }
  }


  def publishIMUData = Future {
    unitIds foreach { camera =>
      imagesPerCamera.foreach { _ =>
        (1 to 10) foreach { _ =>
          DataProducer.writeToKafka(ConfigConstants.imageIMUTopicSubscribe, camera, write(imuData.copy(unitId = camera, timeStampLinux = System.currentTimeMillis())))
          Thread.sleep(10)
        }
      }
    }
  }

  def publishImageObjects(unitId: String, imageId: String, imageUUID: String, objectDetector: String, counter: Int): (ObjectDataMessage, String) = {
    val data = if(counter % 5 == 0){//every fifth image would be empty
      ObjectDataMessage(
        ImageMessage(Array.empty[Int], true, imageId,imageUUID, s"$imageId.jpg" , s"$imageId.jpg", unitId),
        None,
        "yolo3", Instant.now().toEpochMilli
      )
    } else{
      ObjectDataMessage(
        ImageMessage(Array.empty[Int], false, imageId,imageUUID, s"$imageId.jpg" , s"$imageId.jpg", unitId),
        Some((0 to (counter % 10)).toList.map(value => ObjectData(counter+value, value, uniqueObjects(value), 3.45, BoundingBox(1,2,3,4)))),
        "yolo3", Instant.now().toEpochMilli
      )
    }
    DataProducer.writeToKafka(ConfigConstants.imageObjects, imageId, write(data))
    (data, imageId)
  }

  def publishTrackingData(trackingData: List[TrackingData]) = {
    trackingData.foreach(trackData => {
      DataProducer.writeToKafka(ConfigConstants.trackingData, trackData.unitId, write(trackData))
    })
  }

  def generateTrackingData(objects: List[(ObjectDataMessage, String)]): Future[List[TrackingData]] = Future {
    (objects.zipWithIndex flatMap  {case ((imgObject, imageId), index) =>
      if(imgObject.ImageData.imageEmpty){
        None
      } else{
        Some(imgObject.imageObjects.get.map(imageObjectData => {
          TrackingData(imgObject.ImageData.unitId,
            s"${imgObject.ImageData.imageUUID}-$index-${imageObjectData.objId}",
            imageObjectData.objLabelDefinition,
            if(index % 2 == 0) 0.6 else 0.3,
            (1 to new java.util.Random(10).nextInt()).toList map (index2 => {
              Occurrence(s"$imageId",
                imgObject.timestamp,
                BoundingBox(4, DataGenerator.getRandomInt(0, 360), 5, 9),
                if(index % 2 == 0) 0.6 else 0.3)
            }))
        }))
      }
    }).flatten
  }

  def publishAll: Future[Unit] = for{
    objectList <- publishImageHeader
    imgObject <- generateTrackingData(objectList)
  } yield publishTrackingData(imgObject)

  val res = Future.sequence(List(publishGPSData, publishIMUData, publishAll))

  Await.ready(res.map(_ => {
    println("================================ Process completed")
    //WebCamTester.closeCam
    0
  }), Duration.Inf)
}
