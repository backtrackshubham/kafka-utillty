package edu.knoldus

import java.io.File
import java.nio.file.Files

import edu.knoldus.producer.DataProducer
import edu.knoldus.utility.DataGenerator
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

object Monster extends App {
  implicit val formats: DefaultFormats.type = DefaultFormats

  val imagesPerCamera: List[File] = FutureHelper.fileList.flatMap(element => (1 to 10000).map(_ => element))
//  val unitIds = (1 to 100).map(_ => java.util.UUID.randomUUID.toString)
  val unitIds = (1 to 100).map(_ => "a68e0614-f2d2-11e9-8d6b-00044be6503aj")
  val imageHeaderData = DataGenerator.getDataToPublish.imageHeaderData.head
  val gpsData = DataGenerator.getDataToPublish.gpsData.head
  val imuData = DataGenerator.getDataToPublish.imuData.head

  println(s"GPS ${DataGenerator.getDataToPublish.gpsData.length}\n =IMU ${DataGenerator.getDataToPublish.imuData.length}\nImageHeader = ${imagesPerCamera.length}")

  def publishImageHeader = Future {
    unitIds.foreach(unitId => {
      val imageId = java.util.UUID.randomUUID().toString
      imagesPerCamera.zipWithIndex.foreach { case (file, index: Int) =>
        val byteArray = Files.readAllBytes(file.toPath) //excess overhead
        println("Writing image header")
        DataProducer.writeToKafka(ConfigConstants.imageHeaderTopic, unitId, write(imageHeaderData.copy(timestamp = System.currentTimeMillis(), imageId = s"$imageId-$index", unitId = unitId, imageCounter = index)))
        DataProducer.writeImageToKafka(ConfigConstants.imageHeaderTopic, imageId, f"${unitId}_$imageId-$index%05d-R.jpg", byteArray, 0)
        DataProducer.writeImageToKafka(ConfigConstants.imageHeaderTopic, imageId, f"${unitId}_$imageId-$index%05d-R.jpg", byteArray, 0)
//        Thread.sleep(50)
      }
    })
  }

  def publishGPSData = Future {
    unitIds foreach {unitId =>
      imagesPerCamera.foreach { _ =>
        println("Writing GPS data")
        DataProducer.writeToKafka(ConfigConstants.imageGPSTopicSubscribe, unitId, write(gpsData.copy(timestampLinux = System.currentTimeMillis(), unitId = unitId)))
//        Thread.sleep(50)
      }
    }
  }


  def publishIMUData = Future {
    unitIds foreach {unitId =>
      imagesPerCamera.foreach { _ =>
        println("Writing IMU data")
        (1 to 10) foreach { _ =>
          DataProducer.writeToKafka(ConfigConstants.imageIMUTopicSubscribe, unitId, write(imuData.copy(timeStampLinux = System.currentTimeMillis(), unitId = unitId)))
//          Thread.sleep(5)
        }
      }
    }
  }
  //  def publishImageObjects = Future {
  //    unitIds foreach {unitId =>
  //      imagesPerCamera.foreach { _ =>
  //        println("Writing IMU data")
  //        (1 to 10) foreach { _ =>
  //          DataProducer.writeToKafka(ConfigConstants.imageObjects, unitId, write(imuData.copy(timestampLinux = System.currentTimeMillis(), cameraId = unitId)))
  //          Thread.sleep(10)
  //        }
  //      }
  //    }
  //  }
  publishImageHeader
  //  publishGPSData
   publishIMUData
  val res = Future.sequence(List(publishImageHeader, publishIMUData, publishGPSData))

  Await.ready(res.map(_ => {
    println("================================ Process monster completed")
    //    WebCamTester.closeCam
    0
  }), Duration.Inf)

}
