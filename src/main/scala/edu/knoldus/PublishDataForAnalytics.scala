package edu.knoldus

import java.text.SimpleDateFormat
import java.util.Date

import edu.knoldus.model.TrackingLabelData
import edu.knoldus.producer.DataProducer
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

import scala.collection.immutable

object PublishDataForAnalytics extends App {
  implicit val formats: DefaultFormats.type = DefaultFormats

//  val unitId = java.util.UUID.randomUUID().toString
//  val utcMills = 3600000 + 1800000
  val dateStr = "29-09-2019"
  val sdt = new SimpleDateFormat("dd-MM-yyyy")
  val unitId = "73156e8b-4344-44f6-9e8b-4bf16b6aa61f"
  val llLeft = List(120,180,250)
  val durations = List(0.2,0.4,0.6)
  val trackingLabelData = TrackingLabelData("","0","Person", 0,0,"imageId", "imageIdLast",0.5, 1,2,4.5,4L,"N", "W", 8)
  val date = sdt.parse(dateStr)
  var time = date.getTime
//  var time = date.getTime - utcMills
  def publishData: immutable.IndexedSeq[Int] = (0 to 2000) map (index => {
    val data = trackingLabelData.copy(
      unitId = unitId,
      objectId = index.toString,
      objectType = (index % 10).toString,
      lowerYValue = llLeft(index % 3),
      duration = durations(index % 3),
      date = time
    )
    println("now writing")
    DataProducer.writeToKafka("Tracking_Label_Data", unitId,write(data))
    time = time + 720000
    index
  })
  println(publishData)
}
