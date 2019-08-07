package edu.knoldus.model

case class ImageHeaderData(
                            image_Id: String,
                            unit_Id: String,
                            camera_Id: String,
                            imu_Id: Option[IMUData],
                            gps_Id: Option[GPSData],
                            ipaddress: String,
                            timestamp: Long,
                            fNumber: Double,
                            stereo: Boolean,
                            nXPixels: Int,
                            nYPixels: Int,
                            fps: Int,
                            gain: Int,
                            exposure: Int,
                            white_balance_blue: Int,
                            white_balance_red: Int,
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
                    gps_id: String,
                    image_id: String,
                    camera_Id: String,
                    unit_id: String,
                    gps_Counter: Int,
                    gps_Time_UTC: Long,
                    gps_Date: String,
                    gps_Lat: String,
                    gps_Lng: String,
                    gps_Altitude: Double,
                    gps_Angle: Double,
                    gps_Satellites: Int,
                    gps_Speed_knots: Double,
                    gps_Fix: Boolean,
                    gps_quality: Boolean
                  )


case class IMUData(
                    imu_Id: String,
                    camera_id: String,
                    unit_id: String,
                    image_id: String,
                    imu_Time: Int,
                    now_time_since_epoch: Int,
                    linAcc: LinAcc,
                    magnetometer: Magnetometer,
                    gyroscope: Gyro,
                    quaternion: Quaternion
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
