package edu.knoldus.fileoperations

import net.liftweb.json._
import java.util.{Collections, Properties}

import edu.knoldus.ConfigConstants
import edu.knoldus.model.DetectorData
import org.apache.kafka.clients.consumer.KafkaConsumer

object FileConsumer extends App {
  implicit val formats = DefaultFormats

  val props = new Properties()
  props.put("bootstrap.servers", ConfigConstants.kafkaBootStrapServer)
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("auto.offset.reset", "earliest")
  props.put("group.id", "something")
  props.put("enable.auto.commit", "false")
  var count = 0
  var list = List.empty[DetectorData]
  val consumer = new KafkaConsumer[String, String](props)

  def readFromKafka(topic: String = "Image_Data"): Unit = {
    this.consumer.subscribe(Collections.singletonList(topic))
    while (true){
      val record = consumer.poll(5000)
      record.records(topic).forEach(value => {
//        println(value.value())
        val dataFor = parse(value.value())
        val ex = dataFor.extract[DetectorData]
        list = ex :: list
        count = count + 1
        println(s"consumed message number $count")
        val finalList = list.groupBy(_.imageId).map{
          case (key, value) => (key, value.length)
        }.filter{
          case (_, value) => value > 1
        }.map{case (key,value) =>
            s"$key -------------> $value"
        }.mkString("\n")
        println("------------------------")
        println(finalList)
        println("------------------------")
      })
    }
  }
  readFromKafka()

  println(list.length)

}
