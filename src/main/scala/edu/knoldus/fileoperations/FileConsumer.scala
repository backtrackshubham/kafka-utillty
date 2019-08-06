package edu.knoldus.fileoperations

import java.util.{Collections, Properties}

import org.apache.kafka.clients.consumer.KafkaConsumer

object FileConsumer extends App {
  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer")
  props.put("auto.offset.reset", "earliest")
  props.put("enable.auto.commit", "false")
  val consumer = new KafkaConsumer[String, Array[Byte]](props)
  def readFromKafka(topic: String = "test-topic"): Unit = {
    this.consumer.subscribe(Collections.singletonList(topic))
    while (true){
      val record = consumer.poll(5000)
      println(record)
    }
  }

}
