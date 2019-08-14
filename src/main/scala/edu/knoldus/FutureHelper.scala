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

  val currentPath = System.getProperty("user.dir")
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

  val headerList = publisherModel.imageHeaderData



  def publishImageHeader = Future {
    headerList.zip(fileList).foreach{
      case (imageHeaderData: ImageHeaderData, file: File) =>
        val byteArray = Files.readAllBytes(file.toPath)
        println("Writing data")
        DataProducer.writeToKafka("Image_Header2", imageHeaderData.camera_Id, write(imageHeaderData.copy(timestamp = System.currentTimeMillis())))
        DataProducer.writeToKafka("Image_Header2", s"${imageHeaderData.image_Id}-L" , byteArray)
        DataProducer.writeToKafka("Image_Header2", s"${imageHeaderData.image_Id}-R", byteArray)
        Thread.sleep(100)
    }

  }

  def publishGPSData = Future {
    publisherModel.gpsData.foreach(gpsData => {
      DataProducer.writeToKafka("Camera_GPS", gpsData.camera_Id, write(gpsData.copy(gps_Time_UTC = System.currentTimeMillis())))
      Thread.sleep(100)
    })
  }


  def publishIMUData = Future {
    publisherModel.imuData.foreach(imuData => {
      DataProducer.writeToKafka("Camera_IMU", imuData.camera_id, write(imuData.copy(imu_Time = System.currentTimeMillis())))
      Thread.sleep(10)
    })
  }


}
