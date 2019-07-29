package edu.knoldus

import edu.knoldus.model.Models.ImageHeaderData
import edu.knoldus.producer.DataProducer
import edu.knoldus.utility.FileUtility
import net.liftweb.json.Serialization.write
import net.liftweb.json._

object PublishImageObjets {
  def main(args: Array[String]): Unit = {

    implicit val formats = DefaultFormats

    val IMAGE_OBJECTS_TOPIC = "Image_Objects"

    //Please change path accordingly
    val imageHeaderObjects: List[ImageHeaderData] = FileUtility.readImageHeaderJsonFile("/home/freaks/SHUBHAM/Projects/KERB/kerb-kafka-publisher/src/main/resources/object-item.json")

    imageHeaderObjects.foreach(imageHeader => {
      DataProducer.writeToKafka(IMAGE_OBJECTS_TOPIC, imageHeader.imageId, write(imageHeader))
    })

    DataProducer.closeProducer
  }
}
