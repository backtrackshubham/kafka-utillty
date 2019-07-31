package edu.knoldus

import edu.knoldus.model.{GPSData, IMUData}
import edu.knoldus.producer.DataProducer
import edu.knoldus.utility.FileUtility
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

object PublishIMUData {
  def main(args: Array[String]): Unit = {

    implicit val formats = DefaultFormats

    val IMAGE_IMU_TOPIC = "Image_IMU"

    //Please change path accordingly
    val imuData: List[IMUData] = FileUtility.readIMUDataJsonFile("/home/freaks/SHUBHAM/Projects/KERB/kerb-kafka-publisher/src/main/resources/imu.json")

    imuData.foreach(imu => {
      DataProducer.writeToKafka(IMAGE_IMU_TOPIC, imu.image_id, write(imu))
    })

    DataProducer.closeProducer
  }
}
