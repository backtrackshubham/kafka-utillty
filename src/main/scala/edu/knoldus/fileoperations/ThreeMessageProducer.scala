package edu.knoldus.fileoperations

import java.io.File
import java.nio.file.{Files, Paths}
import java.util
import java.util.Properties

import edu.knoldus.model.ImageHeaderData
import edu.knoldus.utility.DataGenerator
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.header.Header
import net.liftweb.json.Serialization.write
import net.liftweb.json._


object ThreeMessageProducer extends App {
  implicit val formats = DefaultFormats

  private val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
//  props.put("bootstrap.servers", "13.90.249.246:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")
  props.put("max.request.size", "1048576")
  private val producer = new KafkaProducer[String, Array[Byte]](props)

  println(s"------------------${System.getProperty("user.dir")}-----------------")

  val currentPath = System.getProperty("user.dir")
  val imagePath = s"$currentPath/src/main/resources/images/"
  val fileList: List[File] = List(new File(s"${imagePath}beginingEnd.png"),
    new File(s"${imagePath}karma.png"),
    new File(s"${imagePath}moksha.png"),
    new File(s"${imagePath}withoutExpectations.png"),
    new File(s"${imagePath}noJudgement.png"),
    new File(s"${imagePath}worldWhatYouImagine.png"),
    new File(s"${imagePath}constantChnage.png"),
    new File(s"${imagePath}realUnreal.png"),
    new File(s"${imagePath}goodEvil.png"),
    new File(s"${imagePath}universe.png"))

  val headerList = DataGenerator.getDataToPublish.imageHeaderData






  def writeToKafka(topic: String, imageId: String, json: Array[Byte]): Unit = {
    producer.send(new ProducerRecord[String, Array[Byte]](topic, imageId, json)).get()
  }


  headerList.foreach(data => {
    val jsonBytes = write(data).getBytes
    writeToKafka("Image_Header", data.imageId, jsonBytes)
  })

  println("=================Press enter to publish images")
  readLine()

  headerList.zip(fileList).foreach{
    case (imageHeaderData: ImageHeaderData, file: File) =>
      val byteArray = Files.readAllBytes(file.toPath)
      println("Writing data")
      writeToKafka("Image_Header", s"${imageHeaderData.imageId}-L.png" , byteArray)
      writeToKafka("Image_Header", s"${imageHeaderData.imageId}-R.png", byteArray)
  }
  //
  //  fileList.foreach(file => {
  //    val byteArray = Files.readAllBytes(file.toPath)
  //    val uuid = java.util.UUID.randomUUID().toString
  //    writeToKafka("test-topic", uuid, byteArray)
  //  })

  def closeProducer = producer.close()
}
