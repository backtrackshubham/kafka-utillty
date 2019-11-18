package edu.knoldus

import java.io.{BufferedReader, FileInputStream, InputStreamReader}
import java.util.Properties

import edu.knoldus.model.ImageObjects
import edu.knoldus.producer.DataProducer
import edu.knoldus.utility.FileUtility
import net.liftweb.json.Serialization.write
import net.liftweb.json._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object PublishImageObjects {
  def main(args: Array[String]): Unit = {

    implicit val formats = DefaultFormats

    val props = new Properties()
    props.put("bootstrap.servers", "192.168.11.172:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("max.request.size", "1048576")
    val producer = new KafkaProducer[String, String](props)

//    val IMAGE_OBJECTS_TOPIC = "Image_Objects"
//    def writeToKafka: Unit = {
//
//      val fstream = new FileInputStream("textfile.txt")
//      val br = new BufferedReader(new InputStreamReader(fstream))
//
//      val strLine = null
//      val record: ProducerRecord[String, String] = new ProducerRecord[String, String](IMAGE_OBJECTS_TOPIC, "test", json)
//    }
    }
    //
    //  fileList.foreach(file => {
    //    val byteArray = Files.readAllBytes(file.toPath)
    //    val uuid = java.util.UUID.randomUUID().toString
    //    writeToKafka("test-topic", uuid, byteArray)
    //  })


}
