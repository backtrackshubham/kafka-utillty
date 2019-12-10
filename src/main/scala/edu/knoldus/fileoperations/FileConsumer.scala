package edu.knoldus.fileoperations

import java.io.{FileOutputStream, OutputStream, PrintWriter}
import java.time.Duration

import net.liftweb.json._
import java.util.{Collections, Date, Properties, UUID}

import edu.knoldus.ConfigConstants
import edu.knoldus.model.{DetectorData, ObjectDataMessage}
import org.apache.kafka.clients.consumer.KafkaConsumer
import net.liftweb.json.Serialization.write

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


object FileConsumer extends App {
  implicit val formats = DefaultFormats

//  val group_id = java.util.UUID.randomUUID.toString
  val group_id = "acebbef7-b4c5-493d-bd8e-749881ee1e77"
  val props = new Properties()
  props.put("bootstrap.servers", "10.2.4.4:9092")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("auto.offset.reset", "earliest")
  props.put("group.id", group_id)
  props.put("enable.auto.commit", "false")
  val consumer = new KafkaConsumer[String, String](props)
//  val writer = new PrintWriter("/home/shubham/Desktop/image-object-usingUtility-update-changes.json", "UTF-8")
//  val badJson = new PrintWriter("/home/shubham/Desktop/badJson-usingUtility-update-changes.json", "UTF-8")
  val correctJson = new PrintWriter("/home/shubham/Desktop/image_object_demo_126.json", "UTF-8")
  val imageNoObjects = new PrintWriter("/home/shubham/Desktop/image_no_object_demo_126.json", "UTF-8")
//  val imageNoObjects = new PrintWriter("/home/shubham/Desktop/correct-json-image-obj-update-changes.json", "UTF-8")

  def readFromKafka(topic: String = "Image_Object_Demo_126"): Future[Unit] = Future {
    var counter = 0
    this.consumer.subscribe(Collections.singletonList(topic))
    println(s"group id is $group_id")
    while (true){
      val record = consumer.poll(50)
      record.records(topic).forEach(value => {
        val parsed: JValue = parse(value.value())
        val data = parsed.extractOpt[ObjectDataMessage]
        data match {
          case Some(value) =>
            val finalData = value.copy(ImageData = value.ImageData.copy(imageFile = Array.empty[Int]))
            val jsonStr = write(finalData)
            println(jsonStr)
            counter = counter + 1
            if(!finalData.ImageData.imageEmpty){
              correctJson.println(jsonStr)
            } else{
              imageNoObjects.println(jsonStr)
            }
            println(counter)
          case None =>
//            badJson.println(value.value())
            println("unable to parse")
        }
      })
    }
  }

  def closeThings = {
    correctJson.close()
    imageNoObjects.close()
  }
  readFromKafka()

  val scanner = new java.util.Scanner(System.in)
  scanner.nextLine()
  closeThings


  //  println(list.length)

}
