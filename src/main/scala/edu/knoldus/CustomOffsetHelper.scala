package edu.knoldus

import java.util.{Collections, Properties}

import edu.knoldus.model.ImageSetMessage
import net.liftweb.json.{DefaultFormats, parse}
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import net.liftweb.json.Serialization.write



object CustomOffsetHelper extends App {

  val producerProps = new Properties()
  producerProps.put("bootstrap.servers", "10.2.4.4:9092")
  producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  producerProps.put("max.request.size", "1048576")
  val producer = new KafkaProducer[String, String](producerProps)

  implicit val formats = DefaultFormats

  val props = new Properties()
  props.put("bootstrap.servers", "10.2.4.4:9092")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("auto.offset.reset", "earliest")
  props.put("group.id", "something-newesst")
  props.put("enable.auto.commit", "false")
  val consumer = new KafkaConsumer[String, String](props)

  def readFromKafka(topic: String = "Image_Aggregate"): Unit = {
    var counter = 0
    this.consumer.subscribe(Collections.singletonList(topic))
    while (true) {
      val record = consumer.poll(5000)
      record.records(topic).forEach(value => {
        println(value.offset())
        if (value.offset() > 11790 && value.offset() < 13938) {
          println(value.value())
          val dataFor = parse(value.value())
          val ex: ImageSetMessage = dataFor.extract[ImageSetMessage]
          writeToKafka(topic, ex.imageUUID, ex.copy(imagesCount = if (ex.imagesCount == 0) 5999 else ex.imagesCount))
        }
      })
    }
  }

  def writeToKafka(topic: String, key: String, message: ImageSetMessage): Unit = {
    val record: ProducerRecord[String, String] = new ProducerRecord[String, String](topic, key, write(message))
    producer.send(record).get()
  }

  readFromKafka()
}
