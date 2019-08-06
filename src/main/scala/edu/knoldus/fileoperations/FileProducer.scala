package edu.knoldus.fileoperations

import java.io.File
import java.nio.file.{Files, Paths}
import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object FileProducer extends App {
  private val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")
  private val producer = new KafkaProducer[String, Array[Byte]](props)

//  val file = new File("/home/freaks/Pictures/Screenshot from 2019-08-06 13-08-31.png")

  val x: Array[Byte] =  Files.readAllBytes(Paths.get("/home/freaks/Pictures/Screenshot from 2019-08-06 13-08-31.png"))
  def writeToKafka(topic: String, imageId: String, json: Array[Byte]): Unit = {
    producer.send(new ProducerRecord[String, Array[Byte]](topic, imageId, json))
  }

  println(s"\nWriting in topic: test-topic \nimageId: name\ndata: \n$x")
  writeToKafka("test-topic", "name", x)

  System.in
  def closeProducer = producer.close()
}
