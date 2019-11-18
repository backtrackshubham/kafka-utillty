package edu.knoldus.utility

import java.io.File
import java.nio.file.{Files, Paths}
import java.util
import java.util.Properties

import edu.knoldus.model.ImageHeaderData
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.header.Header
import net.liftweb.json.Serialization.write
import net.liftweb.json._


object DummyObjectDetector extends App {
  implicit val formats = DefaultFormats

  private val props = new Properties()
  val oDjson = FileUtility.readFile("/home/shubham/SHUBHAM/Projects/KERB/kafka-utillty/src/main/resources/objectDetector.json")
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("max.request.size", "1048576")
  private val producer = new KafkaProducer[String, String](props)
  // def writeToKafka(topic: String, imageId: String, json: String): Unit = {
  (1 to 100) foreach{_ =>
    println("Now Publishing")
    producer.send(new ProducerRecord[String, String]("Image_Objects", "imageId", oDjson)).get()
  }
  // }
//
//  fileList.foreach(file => {
//    val byteArray = Files.readAllBytes(file.toPath)
//    val uuid = java.util.UUID.randomUUID().toString
//    writeToKafka("test-topic", uuid, byteArray)
//  })

  /*def closeProducer = */producer.close()
}
