package edu.knoldus

import java.util.{Collections, Properties}

import edu.knoldus.model.{ImageSetMessage, ObjectDataMessage}
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
  props.put("group.id", "464bbda3-2a49-402e-8e4d-864246fa54fa")
  props.put("enable.auto.commit", "false")
  val consumer = new KafkaConsumer[String, String](props)

  def readFromKafka(topic: String = "Image_Objects"): Unit = {
    var counter = 0
    this.consumer.subscribe(Collections.singletonList(topic))
    while (true) {
      val record = consumer.poll(5000)
      record.records(topic).forEach(value => {
        counter = counter + 1
        if(counter == 1){
          println(s"Started Reading ${value.offset()}")
          println(value.value())
//          println(value.value())
        }
        if(value.value() == null){
          println("Got a null record")
          println(value.offset())
        } else{
          val dataFor = parse(value.value())
          val ex: ObjectDataMessage = dataFor.extract[ObjectDataMessage]
          if(ex.imageData.unitId.equals("d6c331fc-b8a5-4c2e-bdab-62baf754331e")){
            println(s"${value.partition()} ============== ${value.offset()}")
          }
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
