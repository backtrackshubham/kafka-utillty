package edu.knoldus

import java.io.File
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.Date

import edu.knoldus.producer.DataProducer
import edu.knoldus.utility.DataGenerator
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object BombardierData extends App {
  implicit val formats: DefaultFormats.type = DefaultFormats

  val imagesPerCamera: List[File] = FutureHelper.fileList.flatMap(element => (1 to 10).map(_ => element))
  val cameraId = "ASD$1231241"
  val cameraIds = (1 to 10).map(count => s"$cameraId-$count")
  val unitIds = (1 to 10).map(_ => java.util.UUID.randomUUID.toString)
  val imageHeaderData = DataGenerator.getDataToPublish.imageHeaderData.head
  val gpsData = DataGenerator.getDataToPublish.gpsData.head
  val imuData = DataGenerator.getDataToPublish.imuData.head

  println(s"GPS ${DataGenerator.getDataToPublish.gpsData.length}\n =IMU ${DataGenerator.getDataToPublish.imuData.length}\nImageHeader = ${DataGenerator.getDataToPublish.imageHeaderData.length}")

  def publishImageHeader = Future {
    unitIds.foreach(unitId => {
      val imageId = java.util.UUID.randomUUID().toString
      imagesPerCamera.zipWithIndex.foreach { case (file, index: Int) =>
        val byteArray = Files.readAllBytes(file.toPath) //excess overhead
        println("Writing image header")
        DataProducer.writeToKafka(ConfigConstants.imageHeaderTopic, unitId, write(imageHeaderData.copy(timestamp = System.currentTimeMillis(), imageId = s"$imageId-$index", unitId = unitId, imageCounter = index)))
        DataProducer.writeToKafka(ConfigConstants.imageHeaderTopic, s"${unitId}_$imageId-$index-L.png", byteArray)
        DataProducer.writeToKafka(ConfigConstants.imageHeaderTopic, s"${unitId}_$imageId-$index-R.png", byteArray)
        Thread.sleep(100)
      }
    })
  }

  def publishGPSData = Future {
    unitIds foreach {camera =>
      imagesPerCamera.foreach { _ =>
        println("Writing GPS data")
        println(write(gpsData.copy(timestampLinux = System.currentTimeMillis(), unitId = camera)))
        DataProducer.writeToKafka(ConfigConstants.imageGPSTopicSubscribe, camera, write(gpsData.copy(timestampLinux = System.currentTimeMillis(), unitId = camera, timestampGPS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz").format(new Date()))))
        Thread.sleep(100)
      }
    }
  }


  def publishIMUData = Future {
    unitIds foreach {camera =>
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
  //  def publishImageObjects = Future {
  //    cameraIds foreach {camera =>
  //      imagesPerCamera.foreach { _ =>
  //        println("Writing IMU data")
  //        (1 to 10) foreach { _ =>
  //          DataProducer.writeToKafka(ConfigConstants.imageObjects, camera, write(imuData.copy(timestampLinux = System.currentTimeMillis(), cameraId = camera)))
  //          Thread.sleep(10)
  //        }
  //      }
  //    }
  //  }
  publishImageHeader
   publishGPSData
  publishIMUData

  Thread.sleep(600000)

}
