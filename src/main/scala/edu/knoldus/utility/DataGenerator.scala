package edu.knoldus.utility

import edu.knoldus.model.{GPSData, ImageHeaderData}

object DataGenerator {

  case class PublisherModel(imageHeaderData: List[ImageHeaderData],gpsData: List[GPSData])
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
    val gpsData = imageHeaderDataList.flatMap(imageHeaderData => {
      (1 to 10).map(count => {
        GPSData("gpsId",
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
          false)
      }).toList
    })
    PublisherModel(imageHeaderDataList, gpsData)
  }
}
