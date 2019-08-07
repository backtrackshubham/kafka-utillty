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
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")
  props.put("max.request.size", "1048576")
  private val producer = new KafkaProducer[String, Array[Byte]](props)

  println("-----------------------------------")

  val fileList: List[File] = List(new File("/home/freaks/Downloads/chrome_downloads/gita/beginingEnd.png"),
    new File("/home/freaks/Downloads/chrome_downloads/gita/karma.png"),
    new File("/home/freaks/Downloads/chrome_downloads/gita/moksha.png"),
    new File("/home/freaks/Downloads/chrome_downloads/gita/withoutExpectations.png"),
    new File("/home/freaks/Downloads/chrome_downloads/gita/noJudgement.png"),
    new File("/home/freaks/Downloads/chrome_downloads/gita/worldWhatYouImagine.png"),
    new File("/home/freaks/Downloads/chrome_downloads/gita/constantChnage.png"),
    new File("/home/freaks/Downloads/chrome_downloads/gita/realUnreal.png"),
    new File("/home/freaks/Downloads/chrome_downloads/gita/goodEvil.png"),
    new File("/home/freaks/Downloads/chrome_downloads/gita/universe.png"))

  val headerList = DataGenerator.getDataToPublish.imageHeaderData






  def writeToKafka(topic: String, imageId: String, json: Array[Byte]): Unit = {
    producer.send(new ProducerRecord[String, Array[Byte]](topic, imageId, json)).get()
  }


  headerList.foreach(data => {
    val jsonBytes = write(data).getBytes
    writeToKafka("Image_Data", data.image_Id, jsonBytes)
  })

  headerList.zip(fileList).foreach{
    case (imageHeaderData: ImageHeaderData, file: File) =>
      val byteArray = Files.readAllBytes(file.toPath)
      println("Writing data")
      writeToKafka("Image_Data", s"${imageHeaderData.image_Id}-L" , byteArray)
      writeToKafka("Image_Data", s"${imageHeaderData.image_Id}-R", byteArray)
  }
  //
  //  fileList.foreach(file => {
  //    val byteArray = Files.readAllBytes(file.toPath)
  //    val uuid = java.util.UUID.randomUUID().toString
  //    writeToKafka("test-topic", uuid, byteArray)
  //  })

  def closeProducer = producer.close()
}
