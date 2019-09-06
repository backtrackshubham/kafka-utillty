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

case class BoundingBox(lowerX: Int, lowerY: Int, upperX: Int, upperY: Int)


case class DiversityMap(lowerX: Double, lowerY: Double, center: Double, upperX: Double, upperY: Double)


case class GPSData(
                    gpsId: String,
                    imageId: Option[String],
                    timestampLinux: Long,
                    timeUTC: String,
                    date : String,
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

case class ImageAggregated(imageUUID: String, imagesURL: String)