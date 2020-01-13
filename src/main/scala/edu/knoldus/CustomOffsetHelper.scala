package edu.knoldus

import java.util.{Collections, Properties}

import edu.knoldus.model.{BoundingBox, ObjectDataMessage}
import net.liftweb.json.{DefaultFormats, parse}
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import net.liftweb.json.Serialization.write

import scala.util.matching.Regex

case class TrackingData2(
                          unitId: String,
                          objectId: String,
                          objectType: String,
                          imageUUID: Option[String],
                          time: Double,
                          occurrence: List[Occurrence2]
                        )
case class Occurrence2(imageId: String, timestamp: Long, bbox: BoundingBox, trackingConfidence: Double, distance: Option[Double])

object CustomOffsetHelper extends App {

  val IMAGE_UUID_REGEX: Regex = """[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}""".r



  val producerProps = new Properties()
  producerProps.put("bootstrap.servers", "192.168.11.181:9092")
  producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  producerProps.put("max.request.size", "1048576")
  val producer = new KafkaProducer[String, String](producerProps)

  implicit val formats = DefaultFormats

  val props = new Properties()
  props.put("bootstrap.servers", "192.168.11.181:9092")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("auto.offset.reset", "latest")
  props.put("group.id", java.util.UUID.randomUUID().toString)
  props.put("enable.auto.commit", "false")
  val consumer = new KafkaConsumer[String, String](props)

  def readFromKafka(topic: String = "Image_Objects"): Unit = {
    var counter = 0
    this.consumer.subscribe(Collections.singletonList(topic))
    while (true) {
      val record = consumer.poll(5000)
      record.records(topic).forEach(value => {
        val newTrackingData = parse(value.value()).extract[ObjectDataMessage]

        println(s"================== ${newTrackingData.ImageData.imageId}")
//        val imageUUID = IMAGE_UUID_REGEX.findFirstIn(newTrackingData.occurrence.map(_.imageId).distinct.head).fold("")(identity)

//        val finalTrack = if(value.offset() % 5 == 0) newTrackingData.copy(imageUUID = Some(imageUUID), occurrence = newTrackingData.occurrence.map(_.copy(distance = Some(12.36))))
//        else newTrackingData.copy(imageUUID = Some(imageUUID), occurrence = newTrackingData.occurrence.map(_.copy(distance = Some(-1))))

//        writeToKafka("Tracking_Data", value.key(), write(finalTrack))
//        writeToKafka("Image_Objects", value.key(), value.value())

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




  def writeToKafka(topic: String, key: String, message: String): Unit = {
    val record: ProducerRecord[String, String] = new ProducerRecord[String, String](topic, key, message)
    producer.send(record).get()
  }

  readFromKafka()
}
