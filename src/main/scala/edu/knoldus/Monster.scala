package edu.knoldus

import java.io.File
import java.nio.file.Files

import edu.knoldus.producer.DataProducer
import edu.knoldus.utility.DataGenerator
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Monster extends App {
  implicit val formats: DefaultFormats.type = DefaultFormats

  val imagesPerCamera: List[File] = FutureHelper.fileList.flatMap(element => (1 to 10000).map(_ => element))
  val cameraId = "ASD$1231241"
  val cameraIds = (1 to 100).map(count => s"$cameraId-$count")
  val imageHeaderData = DataGenerator.getDataToPublish.imageHeaderData.head
  val gpsData = DataGenerator.getDataToPublish.gpsData.head
  val imuData = DataGenerator.getDataToPublish.imuData.head

  println(s"GPS ${DataGenerator.getDataToPublish.gpsData.length}\n =IMU ${DataGenerator.getDataToPublish.imuData.length}\nImageHeader = ${imagesPerCamera.length}")

  def publishImageHeader = Future {
    cameraIds.foreach(camera => {
      val imageId = java.util.UUID.randomUUID().toString
      imagesPerCamera.zipWithIndex.foreach { case (file, index: Int) =>
        val byteArray = Files.readAllBytes(file.toPath) //excess overhead
        println("Writing image header")
        DataProducer.writeToKafka(ConfigConstants.imageHeaderTopic, camera, write(imageHeaderData.copy(timestamp = System.currentTimeMillis(), imageId = s"$imageId-$index", cameraId = camera)))
        DataProducer.writeToKafka(ConfigConstants.imageHeaderTopic, s"$imageId-$index-L.png", byteArray)
        DataProducer.writeToKafka(ConfigConstants.imageHeaderTopic, s"$imageId-$index-R.png", byteArray)
        Thread.sleep(100)
      }
    })
  }

  def publishGPSData = Future {
    cameraIds foreach {camera =>
      imagesPerCamera.foreach { _ =>
        println("Writing GPS data")
        DataProducer.writeToKafka(ConfigConstants.imageGPSTopicSubscribe, camera, write(gpsData.copy(timestampLinux = System.currentTimeMillis(), cameraId = camera)))
        Thread.sleep(100)
      }
    }
  }


  def publishIMUData = Future {
    cameraIds foreach {camera =>
      imagesPerCamera.foreach { _ =>
        println("Writing IMU data")
        (1 to 10) foreach { _ =>
          DataProducer.writeToKafka(ConfigConstants.imageIMUTopicSubscribe, camera, write(imuData.copy(timeStampLinux = System.currentTimeMillis(), unitId = camera)))
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
  //  publishGPSData
  //  publishIMUData

  Thread.sleep(600000)

}
