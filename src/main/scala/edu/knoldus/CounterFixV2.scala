package edu.knoldus

import java.util
import java.util.{Collections, Properties}
import net.liftweb.json.DefaultFormats
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.header.Header

import scala.util.Try

object CounterFixV2 extends App {

  val consumerGroupId = "1e4b8774-3028-54bc-b238-5e3c1d2bf3f5"

  val producerProps = new Properties()
  producerProps.put("bootstrap.servers", "10.3.2.4:9092")
  producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  producerProps.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")
  producerProps.put("max.request.size", "1048576")
  val producer = new KafkaProducer[String, Array[Byte]](producerProps)

  implicit val formats = DefaultFormats

  val props = new Properties()
  props.put("bootstrap.servers", "10.3.2.4:9092")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer")
  props.put("auto.offset.reset", "earliest")
  props.put("group.id", consumerGroupId)
  props.put("enable.auto.commit", "false")
  val consumer = new KafkaConsumer[String, Array[Byte]](props)

  def readFromKafka(topic: String = "Image_Message_Test"): Unit = {
    var counter = 0
    this.consumer.subscribe(Collections.singletonList(topic))
    while (true) {
      val record = consumer.poll(500)
      record.records(topic).forEach(value => {
        println("got records")
        if (value.key().contains("bec5746c-70d1-4a64-849d-76814a0808be")) {
          val messageKey = value.headers().headers("image-key")
          val string = Try(new String(messageKey.iterator().next().value())).fold(ex =>{
            ""
          },identity)
          if(!string.isEmpty){
            writeImageToKafka("Image_Message_Fix", value.key(), string,value.value())
          }
        }


        //        counter = counter + 1
        //        if(counter == 1){
        //          println(s"Started Reading ${value.offset()}")
        //          println(value.value())
        ////          println(value.value())
        //        }
        //        if(value.value() == null){
        //          println("Got a null record")
        //          println(value.offset())
        //        } else{
        //          val dataFor = parse(value.value())
        //          val ex: ObjectDataMessage = dataFor.extract[ObjectDataMessage]
        //          if(ex.ImageData.unitId.equals("d6c331fc-b8a5-4c2e-bdab-62baf754331e")){
        //            println(s"${value.partition()} ============== ${value.offset()}")
        //          }
        //        }
      })
    }
  }




  def writeImageToKafka(topic: String, key: String, messageHeaders: String, json: Array[Byte]): Unit = {
    val head: Header = new Header(){
      override def key(): String = "image-key"
      override def value(): Array[Byte] = messageHeaders.getBytes()
    }
    val headers = new util.ArrayList[Header]()
    headers.add(head)
    producer.send(new ProducerRecord[String, Array[Byte]](topic, null, key, json, headers)).get()
  }

  readFromKafka()
}
