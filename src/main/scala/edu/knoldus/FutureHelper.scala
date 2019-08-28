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
  val imagePath = s"$currentPath/src/main/resources/images/"
  val fileList: List[File] = List(new File(s"${imagePath}beginingEnd.png"),
    new File(s"${imagePath}karma.png"),
    new File(s"${imagePath}moksha.png"),
    new File(s"${imagePath}withoutExpectations.png"),
    new File(s"${imagePath}noJudgement.png"),
    new File(s"${imagePath}worldWhatYouImagine.png"),
    new File(s"${imagePath}constantChnage.png"),
    new File(s"${imagePath}realUnreal.png"),
    new File(s"${imagePath}goodEvil.png"),
    new File(s"${imagePath}universe.png"))

  val headerList: List[ImageHeaderData] = publisherModel.imageHeaderData



  def publishImageHeader = Future {
    headerList.zip(fileList).zipWithIndex.foreach{
      case ((imageHeaderData: ImageHeaderData, file: File), index) =>
        val byteArray = Files.readAllBytes(file.toPath)
        println("Writing data")
        DataProducer.writeToKafka("Image_Header", imageHeaderData.cameraId, write(imageHeaderData.copy(timestamp = System.currentTimeMillis())))
        DataProducer.writeToKafka("Image_Header", s"${imageHeaderData.imageId}-L.png" , byteArray)
        DataProducer.writeToKafka("Image_Header", s"${imageHeaderData.imageId}-R.png", byteArray)
        Thread.sleep(100)
    }

  }

  def publishGPSData = Future {
    publisherModel.gpsData.foreach(gpsData => {
      DataProducer.writeToKafka("Camera_GPS", gpsData.cameraId, write(gpsData.copy(timestampLinux = System.currentTimeMillis())))
      Thread.sleep(100)
    })
  }


  def publishIMUData = Future {
    publisherModel.imuData.foreach(imuData => {
      DataProducer.writeToKafka("Camera_IMU", imuData.cameraId, write(imuData.copy(timestampLinux = System.currentTimeMillis())))
      Thread.sleep(10)
    })
  }

  def publishImageObjects = Future{
    publisherModel.imageObjects.foreach{imageObject =>
      DataProducer.writeToKafka("Image_Objects", imageObject.imageId, write(imageObject))
      Thread.sleep(10)
    }
  }


  def publishSingleImageHeader = Future {
    headerList.zip(fileList).zipWithIndex.head match {
      case ((imageHeaderData: ImageHeaderData, file: File), index) =>
        val byteArray = Files.readAllBytes(file.toPath)
        println("Writing data")
        DataProducer.writeToKafka("Image_Header", imageHeaderData.cameraId, write(imageHeaderData.copy(timestamp = System.currentTimeMillis())))
        DataProducer.writeToKafka("Image_Header", s"${imageHeaderData.imageId}-L.png" , byteArray)
        DataProducer.writeToKafka("Image_Header", s"${imageHeaderData.imageId}-R.png", byteArray)
        Thread.sleep(100)
    }

  }

  def publishSingleGPSData = Future {
    publisherModel.gpsData.head match {
      case gpsData => {
        DataProducer.writeToKafka("Camera_GPS", gpsData.cameraId, write(gpsData.copy(timestampLinux = System.currentTimeMillis())))
        Thread.sleep(100)
      }
    }
  }


  def publishSingleIMUData = Future {
    publisherModel.imuData.head match {
      case imuData => {
        DataProducer.writeToKafka("Camera_IMU", imuData.cameraId, write(imuData.copy(timestampLinux = System.currentTimeMillis())))
        Thread.sleep(10)
      }
    }
  }

  def publishSingleImageObjects = Future{
    publisherModel.imageObjects.head match {case imageObject =>
      DataProducer.writeToKafka("Image_Objects", imageObject.imageId, write(imageObject))
      Thread.sleep(10)
    }
  }

}
