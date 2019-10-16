package edu.knoldus

import java.io.File
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date

import edu.knoldus.model._
import edu.knoldus.producer.DataProducer
import edu.knoldus.utility.{DataGenerator, FileUtility}
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object BombardierData extends App {
  implicit val formats: DefaultFormats.type = DefaultFormats
  val lambda = (x: Int, y: Int) => (x + y, x - y, x * y, x / y)
  val uniqueObjects = List("Person","Car", "Building", "Bus", "Pole", "Bag", "Drone", "Truck", "Person", "Person")
  val imagesPerCamera: List[File] = FutureHelper.fileList.flatMap(element => (1 to 10).map(_ => element))
  val cameraId = "ASD$1231241"
//  val cameraIds = (1 to 10).map(count => s"$cameraId-$count")
   val unitIds = (1 to 2).toList.map(_ => "77e5afa2-d882-11e9-994a-00044be64e82")
// val unitIds = (1 to 10).toList.map(_ => java.util.UUID.randomUUID.toString)
  val imageHeaderData = DataGenerator.getDataToPublish.imageHeaderData.head
  val gpsData = DataGenerator.getDataToPublish.gpsData.head
  val imuData = DataGenerator.getDataToPublish.imuData.head

  println(s"GPS ${DataGenerator.getDataToPublish.gpsData.length}\n =IMU ${DataGenerator.getDataToPublish.imuData.length}\nImageHeader = ${DataGenerator.getDataToPublish.imageHeaderData.length}")

  def publishImageHeader: Future[List[(ObjectDataMessage, String)]] = Future {
    val objectDetector = java.util.UUID.randomUUID.toString
    unitIds.flatMap (unitId => {
      val imageId = java.util.UUID.randomUUID().toString
      imagesPerCamera.zipWithIndex.flatMap { case (file, index: Int) =>
        val byteArray = Files.readAllBytes(file.toPath) //excess overhead
//        println("Writing image header")
//        println(write(imageHeaderData.copy(timestamp = System.currentTimeMillis(), imageId = s"$imageId-$index%05d", unitId = unitId, imageCounter = index)))
        DataProducer.writeToKafka(ConfigConstants.imageHeaderTopic, unitId, write(imageHeaderData.copy(timestamp = System.currentTimeMillis(), imageId = s"$imageId-$index", unitId = unitId, imageCounter = index)))
        DataProducer.writeToKafka(ConfigConstants.imageTopic, f"${unitId}_$imageId-$index%05d-L.png", byteArray)
        DataProducer.writeToKafka(ConfigConstants.imageTopic, f"${unitId}_$imageId-$index%05d-R.png", byteArray)
        Thread.sleep(100)
        publishImageObjects(unitId, s"$imageId-$index", objectDetector)
      }
    })
  }

  def publishGPSData = Future {
    unitIds foreach { camera =>
      imagesPerCamera.foreach { _ =>
//        println("Writing GPS data")
//        println(write(gpsData.copy(timestampLinux = System.currentTimeMillis(), unitId = camera)))
        DataProducer.writeToKafka(ConfigConstants.imageGPSTopicSubscribe, camera,
          write(gpsData.copy(timestampLinux = System.currentTimeMillis(),
          unitId = camera,
          timestampGPS = FileUtility.GPS_DATE_FORMATTER.format(Instant.now()))))
        Thread.sleep(100)
      }
    }
  }


  def publishIMUData = Future {
    unitIds foreach { camera =>
      imagesPerCamera.foreach { _ =>
//        println("Writing IMU data")
        (1 to 10) foreach { _ =>
//          println(write(imuData.copy(unitId = camera, timeStampLinux = System.currentTimeMillis())))
          DataProducer.writeToKafka(ConfigConstants.imageIMUTopicSubscribe, camera, write(imuData.copy(unitId = camera, timeStampLinux = System.currentTimeMillis())))
          Thread.sleep(10)
        }
      }
    }
  }

  def publishImageObjects(unitId: String, imageId: String, objectDetector: String): List[(ObjectDataMessage, String)] = {
    val imageObject = ObjectDataMessage(ImageMessage("0",imageId,""), ObjectData(1, 1, "somelable"), 45.36, BoundingBox(1, 2, 3, 6), "", "", 0)
    (1 to 5).toList map (count => {
      val imgObject = imageObject.copy(ObjectDataMessage = imageObject.ObjectDataMessage.copy(count, count + 1), unitId = unitId, objectDetectorId = objectDetector, timestamp = System.currentTimeMillis())
//      println(write(imgObject))
      DataProducer.writeToKafka(ConfigConstants.imageObjects, imageId, write(imgObject))
      (imgObject, imageId)
    })
  }

  def publishTrackingData(trackingData: List[TrackingData]) = {
    trackingData.foreach(trackData => {
      println("=============== Publishing Tracking data")
      DataProducer.writeToKafka(ConfigConstants.trackingData, trackData.unitId, write(trackData))
    })
  }

  def generateTrackingData(objects: List[(ObjectDataMessage, String)]): Future[List[TrackingData]] = Future {
//    println(s"=============== generating Tracking data ${objects.length}")
    objects.zipWithIndex map {case ((imgObject, imageId), index) =>
//    println(s"=============== generated Tracking data ${index}")
     TrackingData(imgObject.unitId,
        s"${imgObject.ImageData.imageUUID}-$index",
        uniqueObjects(index % 10),
        if(index % 2 == 0) 0.6 else 0.3,
        (1 to imgObject.ObjectDataMessage.objId * 2).toList map (index2 => {
          Occurrence(s"$imageId",
            imgObject.timestamp,
            BoundingBox(4, DataGenerator.getRandomInt(0, 360), 5, 9),
          if(index % 2 == 0) 0.6 else 0.3)
        }))
    }
//    println(s"=============== finished Tracking data ${data.length}")
  }
  val imageObjects = publishImageHeader
  publishGPSData
  publishIMUData
  for{
    objectList <- imageObjects
    imgObject <- generateTrackingData(objectList)
  } yield publishTrackingData(imgObject)

  Thread.sleep(600000)


}
