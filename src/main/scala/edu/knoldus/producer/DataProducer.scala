package edu.knoldus.producer


import java.util
import java.util.Properties

import edu.knoldus.ConfigConstants
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.header.Header

object DataProducer {
  private val props = new Properties()
  // props.put("bootstrap.servers", "13.90.249.246:9092")
  props.put("bootstrap.servers", ConfigConstants.kafkaBootStrapServer)
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  private val producer = new KafkaProducer[String, String](props)

  private val byteArrayProps = new Properties()
  byteArrayProps.put("bootstrap.servers", ConfigConstants.kafkaBootStrapServer)
  byteArrayProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  byteArrayProps.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")
  byteArrayProps.put("max.request.size", "1048576")
  private val byteArrayProducer = new KafkaProducer[String, Array[Byte]](byteArrayProps)


  def writeToKafka(topic: String, key: String, json: String): Unit = {
    val record: ProducerRecord[String, String] = new ProducerRecord[String, String](topic, key, json)
    producer.send(record).get()
  }

  def writeToKafkaTest(topic: String, key: String, json: String): Unit = {
    val record: ProducerRecord[String, String] = new ProducerRecord[String, String](topic, key, null)
    producer.send(record).get()
  }

  def writeToKafkaWithNegativeTime(topic: String, key: String, json: String): Unit = {
    val record: ProducerRecord[String, String] = new ProducerRecord[String, String](topic, 2, -1L, key, json)
    producer.send(record)
  }

  def writeImageToKafka(topic: String, key: String, messageHeaders: String, json: Array[Byte]): Unit = {
    val head: Header = new Header(){
      override def key(): String = "image-key"
      override def value(): Array[Byte] = messageHeaders.getBytes()
    }
    val headers = new util.ArrayList[Header]()
    headers.add(head)
    byteArrayProducer.send(new ProducerRecord[String, Array[Byte]](topic, null, key, json, headers)).get()
  }


  def closeProducer = {
    producer.close()
    byteArrayProducer.close()
  }
}
