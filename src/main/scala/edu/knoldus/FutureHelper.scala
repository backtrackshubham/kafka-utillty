package edu.knoldus

import java.io.File
import java.nio.file.Files

import edu.knoldus.model.ImageHeaderData
import edu.knoldus.producer.DataProducer
import edu.knoldus.utility.DataGenerator
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object FutureHelper {
  implicit val formats: DefaultFormats.type = DefaultFormats

  val publisherModel: DataGenerator.PublisherModel = DataGenerator.getDataToPublish

  val currentPath: String = System.getProperty("user.dir")
  val imagePath = s"resources/images/fifty-kb/"
  val fileList: List[File] = List(new File(s"${imagePath}beginingEnd.jpg"),
    new File(s"${imagePath}karma.jpg"),
    new File(s"${imagePath}moksha.jpg"),
    new File(s"${imagePath}withoutExpectations.jpg"),
    new File(s"${imagePath}noJudgement.jpg"),
    new File(s"${imagePath}worldWhatYouImagine.jpg"),
    new File(s"${imagePath}constantChnage.jpg"),
    new File(s"${imagePath}realUnreal.jpg"),
    new File(s"${imagePath}goodEvil.jpg"),
    new File(s"${imagePath}universe.jpg"))

  val headerList: List[ImageHeaderData] = publisherModel.imageHeaderData



  def publishImageHeader = Future {
    headerList.zip(fileList).zipWithIndex.foreach{
      case ((imageHeaderData: ImageHeaderData, file: File), index) =>

        val byteArray = Files.readAllBytes(file.toPath)
        println("Writing data")
        println(s"publishing header for ${imageHeaderData.imageId}")
        DataProducer.writeToKafka(ConfigConstants.imageHeaderTopic, imageHeaderData.cameraId, write(imageHeaderData.copy(timestamp = System.currentTimeMillis())))
        DataProducer.writeImageToKafka(ConfigConstants.imageHeaderTopic, imageHeaderData.imageId, s"${imageHeaderData.unitId}_${imageHeaderData.imageId}-L.png" , byteArray)
        DataProducer.writeImageToKafka(ConfigConstants.imageHeaderTopic, imageHeaderData.imageId,s"${imageHeaderData.unitId}_${imageHeaderData.imageId}-R.png", byteArray)
        Thread.sleep(100)
    }

  }

  def publishGPSData = Future {
    publisherModel.gpsData.foreach(gpsData => {
      DataProducer.writeToKafka(ConfigConstants.imageGPSTopicSubscribe, gpsData.unitId, write(gpsData.copy(timestampLinux = System.currentTimeMillis())))
      Thread.sleep(100)
    })
  }


  def publishIMUData = Future {
    publisherModel.imuData.foreach(imuData => {
      DataProducer.writeToKafka(ConfigConstants.imageIMUTopicSubscribe, imuData.unitId, write(imuData.copy(timeStampLinux = System.currentTimeMillis())))
      Thread.sleep(10)
    })
  }

  def publishImageObjects = Future{
    println(write(publisherModel.imageObjects))
    publisherModel.imageObjects.foreach{imageObject =>
      println(s"publishing objects for ${imageObject.imageId}")
      DataProducer.writeToKafka("Image_Objects", imageObject.imageId, write(imageObject))
      Thread.sleep(10)
    }
  }


  def publishSingleImageHeader = Future {
    headerList.zip(fileList).zipWithIndex.head match {
      case ((imageHeaderData: ImageHeaderData, file: File), index) =>
        val byteArray = Files.readAllBytes(file.toPath)
        println("Writing data")
        DataProducer.writeToKafka(ConfigConstants.imageHeaderTopic, imageHeaderData.cameraId, write(imageHeaderData.copy(timestamp = System.currentTimeMillis())))
        DataProducer.writeImageToKafka(ConfigConstants.imageHeaderTopic, imageHeaderData.imageId,s"${imageHeaderData.imageId}-L.png" , byteArray)
        DataProducer.writeImageToKafka(ConfigConstants.imageHeaderTopic, imageHeaderData.imageId,s"${imageHeaderData.imageId}-R.png", byteArray)
        Thread.sleep(100)
    }

  }

  def publishSingleGPSData = Future {
    publisherModel.gpsData.head match {
      case gpsData => {
        DataProducer.writeToKafka(ConfigConstants.imageGPSTopicSubscribe, gpsData.unitId, write(gpsData.copy(timestampLinux = System.currentTimeMillis())))
        Thread.sleep(100)
      }
    }
  }


  def publishSingleIMUData = Future {
    publisherModel.imuData.head match {
      case imuData => {
        DataProducer.writeToKafka(ConfigConstants.imageIMUTopicSubscribe, imuData.unitId, write(imuData.copy(timeStampLinux = System.currentTimeMillis())))
        Thread.sleep(10)
      }
    }
  }

  def publishSingleImageObjects = Future{
    publisherModel.imageObjects.head match {case imageObject =>
      DataProducer.writeToKafka(ConfigConstants.imageObjects, imageObject.imageId, write(imageObject))
      Thread.sleep(10)
    }
  }

}
