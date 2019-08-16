package edu.knoldus

import edu.knoldus.model.GPSData
import edu.knoldus.producer.DataProducer
import edu.knoldus.utility.FileUtility
import net.liftweb.json.Serialization.write
import net.liftweb.json._

object PublishGPSDataHeader {
  def main(args: Array[String]): Unit = {

    implicit val formats = DefaultFormats

    val IMAGE_GPS_TOPIC = "Image_GPS"

    //Please change path accordingly
    val gpsData: List[GPSData] = FileUtility.readGPSDataJsonFile("/home/freaks/SHUBHAM/Projects/KERB/kerb-kafka-publisher/src/main/resources/gps.json")

    gpsData.foreach(gps => {
      DataProducer.writeToKafka(IMAGE_GPS_TOPIC, gps.imageId, write(gps))
    })

    DataProducer.closeProducer
  }
}
