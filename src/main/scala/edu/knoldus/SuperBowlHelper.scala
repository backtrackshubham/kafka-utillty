package edu.knoldus

import java.util.Properties

import edu.knoldus.CustomOffsetHelper.producer
import edu.knoldus.model.{ImageAggregate2, TrackingComplete}
import edu.knoldus.utility.FileUtility
import net.liftweb.json.DefaultFormats
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

object SuperBowlHelper extends App {
  implicit val formats: DefaultFormats.type = DefaultFormats

  val producerProps = new Properties()
  producerProps.put("bootstrap.servers", "10.2.4.4:9092")
  producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  producerProps.put("max.request.size", "1048576")
  val producer = new KafkaProducer[String, String](producerProps)

  val imuData: List[ImageAggregate2] = FileUtility.readImageAggregate2File("/home/shubham/SHUBHAM/Projects/KERB/kafka-utillty/src/main/resources/super-bowl-image-aggregate.json")


  imuData.foreach(aggregateData => {
    println(aggregateData.hdfsCreationTime)
//    writeToKafka("Image_Aggregate_Super_2", aggregateData.imageUUID, write(aggregateData))
  })

  def writeToKafka(topic: String, key: String, message: String): Unit = {
    val record: ProducerRecord[String, String] = new ProducerRecord[String, String](topic, key, message)
    producer.send(record).get()
  }

  println("============== publish complete")

}
