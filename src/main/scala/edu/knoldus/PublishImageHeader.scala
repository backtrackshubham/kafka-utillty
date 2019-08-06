package edu.knoldus

import edu.knoldus.model.ImageHeaderData
import edu.knoldus.producer.DataProducer
import edu.knoldus.utility.FileUtility
import net.liftweb.json.Serialization.write
import net.liftweb.json._

object PublishImageHeader {
  def main(args: Array[String]): Unit = {

    implicit val formats = DefaultFormats

    val IMAGE_HEADER_TOPIC = "Image_Header"

    //Please change path accordingly
    val imageHeaderObjects: List[ImageHeaderData] = FileUtility.readImageHeaderJsonFile("/home/freaks/SHUBHAM/Projects/KERB/kerb-kafka-publisher/src/main/resources/image-header.json")

    imageHeaderObjects.foreach(imageHeader => {
      DataProducer.writeToKafka(IMAGE_HEADER_TOPIC, imageHeader.image_Id, write(imageHeader))
    })

    DataProducer.closeProducer
  }
}
