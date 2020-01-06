package edu.knoldus

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.temporal.ChronoUnit
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

  val sdt = new SimpleDateFormat("dd-MM-yyyy")
  val dateStr = sdt.format(new Date(Instant.now().minus(7, ChronoUnit.DAYS).toEpochMilli))
  val unitId = "21a96b14-0c5e-11ea-a3b9-00044be6503a"
  val imageIds = (1 to 10).toList.map(_ => java.util.UUID.randomUUID.toString)
  val uniqueObjects = List("person","Car", "Building", "Bus", "Pole", "Bag", "Drone", "Truck", "person", "person")
  val llLeft = List(120,180,250)
  val durations = List(0.2,0.4,0.6)
  val trackingLabelData = TrackingLabelData("","0","Person", 0,0,"imageId", "imageIdLast",0.5, 1,2,4.5,4L,"N", "W", 8, 45.56)
  val date = sdt.parse(dateStr)
  var time = date.getTime
//  var time = date.getTime - utcMills
  def publishData: immutable.IndexedSeq[Int] = (0 to 2000) map (index => {
    val data = trackingLabelData.copy(
      unitId = unitId,
      objectId = s"${imageIds(index % 10)}-$index",
      objectType = uniqueObjects(index % 10),
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
