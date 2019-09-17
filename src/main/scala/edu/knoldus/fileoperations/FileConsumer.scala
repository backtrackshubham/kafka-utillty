package edu.knoldus.fileoperations

import java.io.{FileOutputStream, OutputStream}
import java.time.Duration

import net.liftweb.json._
import java.util.{Collections, Date, Properties, UUID}

import edu.knoldus.ConfigConstants
import edu.knoldus.model.DetectorData
import org.apache.kafka.clients.consumer.KafkaConsumer

object FileConsumer extends App {
  implicit val formats = DefaultFormats

  val props = new Properties()
  props.put("bootstrap.servers", "10.2.4.4:9092")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer")
  props.put("auto.offset.reset", "latest")
  props.put("group.id", "something-newesst")
  props.put("enable.auto.commit", "false")
  val consumer = new KafkaConsumer[String, Array[Byte]](props)

  def readFromKafka(topic: String = "Image_Message_TEST"): Unit = {
    var counter = 0
    this.consumer.subscribe(Collections.singletonList(topic))
    while (true){
      val record = consumer.poll(5000)
      record.records(topic).forEach(value => {
        println(value.offset())
        if(value.offset() > 75000){
          counter += 1
          val os: OutputStream = new FileOutputStream(s"/home/freaks/Desktop/sequence-read/file-${new Date(value.timestamp())}.jpg")
          os.write(value.value())
          os.close()
        }
      })
    }
  }
  readFromKafka()
  

//  println(list.length)

}
