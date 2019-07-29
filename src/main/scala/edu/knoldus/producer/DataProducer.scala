package edu.knoldus.producer


import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object DataProducer {
  private val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  private val producer = new KafkaProducer[String, String](props)

  def writeToKafka(topic: String, imageId: String, json: String): Unit = {
    producer.send(new ProducerRecord[String, String](topic,imageId,json))
  }

  def closeProducer = producer.close()
}
