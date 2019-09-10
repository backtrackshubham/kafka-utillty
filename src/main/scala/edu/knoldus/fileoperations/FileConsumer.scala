package edu.knoldus.fileoperations

import java.time.Duration

import net.liftweb.json._
import java.util.{Collections, Properties, UUID}

import edu.knoldus.ConfigConstants
import edu.knoldus.model.DetectorData
import org.apache.kafka.clients.consumer.KafkaConsumer

object FileConsumer extends App {
  implicit val formats = DefaultFormats

  val props = new Properties()
  props.put("bootstrap.servers", "10.2.4.4:9092")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("auto.offset.reset", "latest")
  props.put("group.id", "something")
  props.put("enable.auto.commit", "false")
  val consumer = new KafkaConsumer[String, String](props)

  def readFromKafka(topic: String = "Image_Header"): Unit = {
    this.consumer.subscribe(Collections.singletonList(topic))
    while (true){
      val record = consumer.poll(5000)
      record.records(topic).forEach(value => {
        if(!value.key().contains(".jpg"))
        println(value.timestamp())
      })
    }
  }
  readFromKafka()
  

//  println(list.length)

}
