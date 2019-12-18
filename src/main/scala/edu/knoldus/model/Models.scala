package edu.knoldus.model

import org.apache.avro.data.Json

case class ImageHeaderData(
                            imageId: String,
                            unitId: String,
                            cameraId: String,
                            ipAddress: String,
                            timestamp: Long,
                            fNumber: Double,
                            stereo: Boolean,
                            nXPixels: Int,
                            nYPixels: Int,
                            fps: Int,
                            gain: Int,
                            exposure: Int,
                            whiteBalanceBlue: Int,
                            whiteBalanceRed: Int,
                            stereoSep: Option[Double],
                            imageCounter: Long
                          )




case class ImageObjects(imageId: String, objectDetectorId: String, objects: List[ObjectItem], timestamp: String)

//old
case class ObjectItem(
                       objId: Int,
                       imageId: String,
                       objLabel: Int,
                       prob: Double,
                       xDim: Int,
                       yDim: Int,
                       bBox: BoundingBox,
                       bBoxImage: String,
                       disparityMap: Option[String]
                     )

case class ObjectDataMessage(ImageData: ImageMessage,
                             imageObjects: Option[List[ObjectData]],
                             objectDetectorId: String,
                             timestamp: Long)

case class ObjectData(objId: Int, objLabel: Int, objLabelDefinition: String, prob: Double, bBox: BoundingBox, distance:Double)


case class ImageMessage(imageFile: Array[Int], imageEmpty: Boolean, imageId: String, imageUUID: String, imagesDirUrl: String, imageName: String, unitId: String)

case class ImageSetMessage(imagesCount: Long, imageUUID: String, imagesDirUrl: String, unitId: String, firstImageId: String)


case class BoundingBox(lowerLeftX: Int, lowerLeftY: Int, upperRightX: Int, upperRightY: Int)


case class DiversityMap(lowerX: Double, lowerY: Double, center: Double, upperX: Double, upperY: Double)


case class GPSData(
                    gpsId: String,
                    imageId: Option[String],
                    timestampLinux: Long,
                    timestampGPS: String,
                    latitude: Coordinates,
                    latitudeNS: String,
                    longitude: Coordinates,
                    longitudeEW: String,
                    speedKnots: Double,
                    angle: Double,
                    fix: Boolean,
                    imageCounter : Option[Long],
                    unitId: String
                  )

case class Coordinates(degrees: Int, minutes: Double)


case class IMUData(
                    imuId: String,
                    time: Long,
                    timeStampLinux: Long,
                    imageId: Option[String],
                    linAcc: LinAcc,
                    magnetometer: Magnetometer,
                    gyroscope: Gyro,
                    quaternion: Quaternion,
                    unitId: String,
                    imageCounter: Option[Long]
                  )



case class LinAcc(
                   x: Int,
                   y: Int,
                   z: Int
                 )



case class Magnetometer(
                         x: Int,
                         y: Int,
                         z: Int
                       )



case class Gyro(
                 x: Int,
                 y: Int,
                 z: Int
               )



case class Quaternion(
                       x: Int,
                       y: Int,
                       z: Int,
                       w: Int
                     )
case class DetectorData(imageId: String,
                        unitId: String,
                        cameraId: String,
                        timestamp: Long,
                        gpsData: GPSData,
                        imuData: IMUData,
                        imageLeftUrl: String,
                        imageRightUrl: String
                       )

case class TrackingData(
                         unitId: String,
                         objectId: String,
                         objectType: String,
                         time: Double,
                         occurrence: List[Occurrence]
                       )


case class ImageAggregated(imageUUID: String, imagesURL: String)

case class Occurrence(imageId: String, timestamp: Long, bbox: BoundingBox, trackingConfidence: Double)

case class Description(timestamp: Long, trackingConfidence: Double, location: Location, bbox: BoundingBox)

case class Location(x: Double, y: Double)

case class TestData(
                     unitId: String,
                     objectId: String,
                     objectType: String,
                     pictureZone: String,
                     duration: Double,
                     time: Long,
                     lowerLeftXValue: Int,
                     lowerLeftYValue: Int,
                     medianLatitude:String,
                     medianLongitude:String
                   )

case class TrackingLabelData(
                              unitId: String,
                              objectId: String,
                              objectType: String,
                              firstAppearedTime: Long,
                              lastAppearedTime: Long,
                              firstImageId: String,
                              lastImageId: String,
                              maxTrackingConfidence: Double,
                              lowerXValue: Int,
                              lowerYValue: Int,
                              duration : Double,
                              date: Long,
                              latitude : String,
                              longitude : String,
                              totalOccurrences: Int
                            )
