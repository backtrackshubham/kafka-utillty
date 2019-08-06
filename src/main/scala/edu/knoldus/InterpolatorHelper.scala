package edu.knoldus

import edu.knoldus.producer.DataProducer
import edu.knoldus.utility.DataGenerator
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

object InterpolatorHelper extends App {
  implicit val formats: DefaultFormats.type = DefaultFormats

  val publisherModel: DataGenerator.PublisherModel = DataGenerator.getDataToPublish

  publisherModel.imageHeaderData.foreach(imageData => {
    DataProducer.writeToKafka("Image_Header", imageData.image_Id, write(imageData))
  })

  publisherModel.gpsData.foreach(gpsData => {
    DataProducer.writeToKafka("Image_GPS", gpsData.gps_id, write(gpsData))
  })

  DataProducer.closeProducer
}
