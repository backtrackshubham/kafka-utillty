package edu.knoldus

import edu.knoldus.model.TrackingComplete
import edu.knoldus.producer.DataProducer
import edu.knoldus.utility.FileUtility
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

object PublishTrackComplete {
  def main(args: Array[String]): Unit = {

    implicit val formats = DefaultFormats

    val TRACK_COMP_TOPIC = "Track_Complete"

    //Please change path accordingly
    val imuData: List[TrackingComplete] = FileUtility.readTrackCompleteJsonFile("/home/shubham/SHUBHAM/Projects/KERB/Devops/kerb-docker/docker-compose/track-complete.json")

    imuData.foreach(compTrack => {
      DataProducer.writeToKafka(TRACK_COMP_TOPIC, compTrack.imageUUID, write(compTrack))
    })

    DataProducer.closeProducer
  }
}
