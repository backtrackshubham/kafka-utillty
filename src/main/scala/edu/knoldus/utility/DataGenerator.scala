package edu.knoldus.utility

import java.text.SimpleDateFormat
import java.util.Date

import edu.knoldus.model.{GPSData, Gyro, IMUData, ImageHeaderData, LinAcc, Magnetometer, Quaternion}

object DataGenerator {

  case class PublisherModel(imageHeaderData: List[ImageHeaderData], gpsData: List[GPSData], imuData: List[IMUData])
  val getGpsTime: (Long, Int) => Long = (time: Long, count: Int) => if(count % 2 == 0) time + count else time - count
  private def getImageHeaderData: List[ImageHeaderData] = {
    (1 to 10).map(count => {
      val imageId = java.util.UUID.randomUUID().toString
      val cameraId = "ASD$1231241"
      Thread.sleep(100)
      ImageHeaderData(s"$imageId-$count",
        "unitId",
        cameraId,
        "ipAddress",
        System.currentTimeMillis(),
        0.0f,
        false,
        1,
        2,
        3,
        4,
        6,
        5,
        9,
        None,
        count)
    }).toList
  }

  def getDataToPublish: PublisherModel = {
    val imageHeaderDataList = getImageHeaderData
    val gpsDataimuDataList: List[(GPSData, IMUData)] = imageHeaderDataList.flatMap(imageHeaderData => {
      (1 to 10).map(count => {
        (GPSData(
          "gpsId",
          "imageId",
          imageHeaderData.cameraId,
          getGpsTime(imageHeaderData.timestamp,count),
          "153215.456",
          new SimpleDateFormat("ddMMyy").format(new Date()),
          "latitude",
          "N",
          "longitude",
          "W",
          5.6,
          36.96,
          true,
          None,
          None
          ),
         IMUData("imuId",
          imageHeaderData.cameraId,
          getGpsTime(imageHeaderData.timestamp, count + 1),
           getGpsTime(imageHeaderData.timestamp, count + 1),
           "imageId",
           LinAcc(1,2,3),
           Magnetometer(7,8,9),
           Gyro(4,5,6),
           Quaternion(9, 6, 3, 8),
           None,
           None
         ))
      }).toList
    })

    val (gpsList, imuList): (List[GPSData], List[IMUData]) = gpsDataimuDataList
      .foldLeft(List.empty[GPSData], List.empty[IMUData])((splitedData, bothData) => (splitedData._1 ::: List(bothData._1), splitedData._2 ::: List(bothData._2)) )

    PublisherModel(imageHeaderDataList, gpsList, imuList)
  }
}
