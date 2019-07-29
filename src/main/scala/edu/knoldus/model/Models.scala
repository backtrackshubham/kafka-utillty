package edu.knoldus.model

object Models {

  case class ImageHeaderData (
                               imageId: String,
                               unitId: String,
                               cameraId : String,
                               imuId: String,
                               gpsId: String,
                               ipAddress: String,
                               timestamp : String,
                               fNumber: Double,
                               fps: Int,
                               stereo: Boolean,
                               nXPixels: Int,
                               nYPixels: Int,
                               dimX: Int,
                               dimY: Int,
                               stereoSep: Option[Double]
                             )


  case class ImageObjects(imageId : String, objectDetectorId : String, objects : List[ObjectItem], timestamp : String)

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

  case class BoundingBox(lowerX : Int, lowerY : Int, upperX : Int, upperY : Int)

}
