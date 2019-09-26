package edu.knoldus

import java.io.File
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.Date

import edu.knoldus.model._
import edu.knoldus.producer.DataProducer
import edu.knoldus.utility.DataGenerator
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object BombardierData extends App {
  implicit val formats: DefaultFormats.type = DefaultFormats
  val lambda = (x: Int, y: Int) => (x + y, x - y, x * y, x / y)

  val imagesPerCamera: List[File] = FutureHelper.fileList.flatMap(element => (1 to 10).map(_ => element))
  val cameraId = "ASD$1231241"
  val cameraIds = (1 to 10).map(count => s"$cameraId-$count")
  val unitIds = (1 to 2).toList.map(_ => java.util.UUID.randomUUID.toString)
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
        println("Writing image header")
        println(write(imageHeaderData.copy(timestamp = System.currentTimeMillis(), imageId = s"$imageId-$index", unitId = unitId, imageCounter = index)))
        DataProducer.writeToKafka(ConfigConstants.imageHeaderTopic, unitId, write(imageHeaderData.copy(timestamp = System.currentTimeMillis(), imageId = s"$imageId-$index", unitId = unitId, imageCounter = index)))
        DataProducer.writeToKafka(ConfigConstants.imageTopic, s"${unitId}_$imageId-$index-L.png", byteArray)
        DataProducer.writeToKafka(ConfigConstants.imageTopic, s"${unitId}_$imageId-$index-R.png", byteArray)
        Thread.sleep(100)
        publishImageObjects(unitId, s"$imageId-$index", objectDetector)
      }
    })
  }

  def publishGPSData = Future {
    unitIds foreach { camera =>
      imagesPerCamera.foreach { _ =>
        println("Writing GPS data")
        println(write(gpsData.copy(timestampLinux = System.currentTimeMillis(), unitId = camera)))
        DataProducer.writeToKafka(ConfigConstants.imageGPSTopicSubscribe, camera, write(gpsData.copy(timestampLinux = System.currentTimeMillis(), unitId = camera, timestampGPS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()))))
        Thread.sleep(100)
      }
    }
  }


  def publishIMUData = Future {
    unitIds foreach { camera =>
      imagesPerCamera.foreach { _ =>
        println("Writing IMU data")
        (1 to 10) foreach { _ =>
          println(write(imuData.copy(unitId = camera, timeStampLinux = System.currentTimeMillis())))
          DataProducer.writeToKafka(ConfigConstants.imageIMUTopicSubscribe, camera, write(imuData.copy(unitId = camera, timeStampLinux = System.currentTimeMillis())))
          Thread.sleep(10)
        }
      }
    }
  }

  def publishImageObjects(unitId: String, imageId: String, objectDetector: String): List[(ObjectDataMessage, String)] = {
    val imageObject = ObjectDataMessage(1, 1, "somelable", 45.36, BoundingBox(1, 2, 3, 6), "", "", 0)
    (1 to 5).toList map (count => {
      val imgObject = imageObject.copy(count, count + 1, unitId = unitId, objectDetectorId = objectDetector, timestamp = System.currentTimeMillis())
      println(write(imgObject))
      DataProducer.writeToKafka(ConfigConstants.imageObjects, imageId, write(imgObject))
      (imgObject, imageId)
    })
  }

  def publishTrackingData(trackingData: List[TrackingData]) = {
    trackingData.foreach(trackData => {
      DataProducer.writeToKafka(ConfigConstants.trackingData, trackData.unitId, write(trackData))
      val testData = TestData(trackData.unitId,
        trackData.objectId,
        "someType",
        "pictureZone",
        trackData.time,
        trackData.occurrence.head.description.timestamp,
        trackData.occurrence.head.description.bbox.lowerLeftX,
        trackData.occurrence.head.description.bbox.lowerLeftY,
        "medianLatitude","medianLatitude")
      DataProducer.writeToKafka("test_topic", trackData.unitId, write(testData))
    })
  }

  def generateTrackingData(objects: List[(ObjectDataMessage, String)]): Future[List[TrackingData]] = Future {
    objects.zipWithIndex map {case ((imgObject, imageId), index) =>
      TrackingData(imgObject.unitId,
        index,
        index / 10,
        if(index % 2 == 0) 0.6 else 0.3,
        (1 to imgObject.objId * 2).toList map (index2 => {
          Occurrence(s"$imageId",
            Description(imgObject.timestamp,
              if(index % 2 == 0) 0.6 else 0.3,
              Location(4.36 * imgObject.objId, imgObject.objId * 3.36),
              BoundingBox(4, DataGenerator.getRandomInt(0, 360), 5, 9)))
        }))
    }
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
