package edu.knoldus.utility

import edu.knoldus.model.{GPSData, Gyro, IMUData, ImageHeaderData, LinAcc, Magnetometer, Quaternion}

object DataGenerator {

  case class PublisherModel(imageHeaderData: List[ImageHeaderData], gpsData: List[GPSData], imuData: List[IMUData])
  val getGpsTime: (Long, Int) => Long = (time: Long, count: Int) => if(count % 2 == 0) time + count else time - count
  private def getImageHeaderData: List[ImageHeaderData] = {
    (1 to 10).map(_ => {
      val imageId = java.util.UUID.randomUUID().toString
      val cameraId = java.util.UUID.randomUUID().toString
      Thread.sleep(100)
      ImageHeaderData(imageId,
        "unitId",
        cameraId,
        None,
        None,
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
        None,
        None)
    }).toList
  }

  def getDataToPublish: PublisherModel = {
    val imageHeaderDataList = getImageHeaderData
    val gpsDataimuDataList: List[(GPSData, IMUData)] = imageHeaderDataList.flatMap(imageHeaderData => {
      (1 to 10).map(count => {
        (GPSData("gpsId",
          "imageId",
          imageHeaderData.camera_Id,
          imageHeaderData.unit_Id,
          count,
          getGpsTime(imageHeaderData.timestamp,
            count),
          "gpsDate",
          "gpsLat",
          "gpsLong",
          5.6,
          36.96,
          5,
          0.0f,
          true,
          false),
         IMUData("imuId",
          imageHeaderData.camera_Id,
          imageHeaderData.unit_Id,
          "imageId",
          getGpsTime(imageHeaderData.timestamp, count + 1),
          getGpsTime(imageHeaderData.timestamp, count + 1),
           LinAcc(1,2,3),
           Magnetometer(7,8,9),
           Gyro(4,5,6),
           Quaternion(9, 6, 3, 8)))
      }).toList
    })

    val (gpsList, imuList): (List[GPSData], List[IMUData]) = gpsDataimuDataList
      .foldLeft(List.empty[GPSData], List.empty[IMUData])((splitedData, bothData) => (bothData._1 :: splitedData._1,bothData._2 :: splitedData._2) )

    PublisherModel(imageHeaderDataList, gpsList, imuList)
  }
}
