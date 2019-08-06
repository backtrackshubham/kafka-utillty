package edu.knoldus

import edu.knoldus.producer.DataProducer
import edu.knoldus.utility.DataGenerator
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object FutureHelper {
  implicit val formats: DefaultFormats.type = DefaultFormats

  val publisherModel: DataGenerator.PublisherModel = DataGenerator.getDataToPublish

  def publishImageHeader = Future {
    publisherModel.imageHeaderData.foreach(imageData => {
      DataProducer.writeToKafka("Image_Header", imageData.image_Id, write(imageData.copy(timestamp = System.currentTimeMillis())))
      Thread.sleep(100)
    })
  }

  def publishGPSData = Future {
    publisherModel.gpsData.foreach(gpsData => {
      DataProducer.writeToKafka("Image_GPS", gpsData.gps_id, write(gpsData.copy(gps_Time_UTC = System.currentTimeMillis())))
      Thread.sleep(10)
    })
  }
}
