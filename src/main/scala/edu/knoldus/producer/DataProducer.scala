package edu.knoldus.producer


import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object DataProducer {
  private val props = new Properties()
  // props.put("bootstrap.servers", "13.90.249.246:9092")
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  private val producer = new KafkaProducer[String, String](props)

  private val byteArrayProps = new Properties()
   byteArrayProps.put("bootstrap.servers", "localhost:9092")
  // byteArrayProps.put("bootstrap.servers", "13.90.249.246:9092")
  byteArrayProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  byteArrayProps.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")
  byteArrayProps.put("max.request.size", "1048576")
  private val byteArrayProducer = new KafkaProducer[String, Array[Byte]](byteArrayProps)


  def writeToKafka(topic: String, imageId: String, json: String): Unit = {
    producer.send(new ProducerRecord[String, String](topic,imageId,json))
  }

  def writeToKafka(topic: String, imageId: String, json: Array[Byte]): Unit = {
    byteArrayProducer.send(new ProducerRecord[String, Array[Byte]](topic, imageId, json)).get()
  }


  def closeProducer = { producer.close()
    byteArrayProducer.close() }
}
