package edu.knoldus.hdfsutils

import java.util.{Collections, Properties}

import edu.knoldus.ConfigConstants
import edu.knoldus.model.{DetectorData, ImageAggregated}
import net.liftweb.json.DefaultFormats
import org.apache.kafka.clients.consumer.KafkaConsumer
import net.liftweb.json._


object ImageAggregationConsumer extends App {
  implicit val formats = DefaultFormats
  val props = new Properties()
  props.put("bootstrap.servers", ConfigConstants.kafkaBootStrapServer)
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("auto.offset.reset", "earliest")
  props.put("group.id", "something")
  props.put("enable.auto.commit", "false")
  val consumer = new KafkaConsumer[String, String](props)

  def readFromKafka(topic: String = "Image_Aggregated"): Unit = {
    this.consumer.subscribe(Collections.singletonList(topic))
    while (true){
      val record = consumer.poll(5000)
      record.records(topic).forEach(value => {
                println("consumed message")
        val dataFor = parse(value.value())
        val ex: ImageAggregated = dataFor.extract[ImageAggregated]
        ConnectionProvider.readPathAndSaveToDir(ex)
      })
    }
  }

  readFromKafka()
}
