package edu.knoldus.fileoperations

import java.io.File
import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object FileProducer extends App {
object FileProducer extends App {
  private val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")
  private val producer = new KafkaProducer[String, ByteArray](props)

  val file = new File("")

  val x = ???
  def writeToKafka(topic: String, imageId: String, json: String): Unit = {
    producer.send(new ProducerRecord[String, String](topic, imageId, json))
  }

  def closeProducer = producer.close()
}
