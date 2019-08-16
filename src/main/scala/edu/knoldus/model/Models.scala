package edu.knoldus.model

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
                            hdfsUrlLeft: Option[String],
                            hdfsUrlRight: Option[String]
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
                    imageId: String,
                    cameraId: String,
                    timestampLinux: Long,
                    timeUTC: Long,
                    date : String,
                    latitude: String,
                    latitudeNS: String,
                    longitude: String,
                    longitudeEW: String,
                    speedKnots: Double,
                    angle: Double,
                    fix: Boolean,
                    imageHeaderTimestamp : Option[Long],
                    unitId: Option[String]
                  )


case class IMUData(
                    imuId: String,
                    cameraId: String,
                    imageId: String,
                    time: Long,
                    timestampLinux: Long,
                    linAcc: LinAcc,
                    magnetometer: Magnetometer,
                    gyroscope: Gyro,
                    quaternion: Quaternion,
                    imageHeaderTimestamp : Option[Long],
                    unitId: Option[String]
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
                 alpha: Int,
                 beta: Int,
                 gamma: Int
               )



case class Quaternion(
                       x: Int,
                       y: Int,
                       z: Int,
                       w: Int
                     )
