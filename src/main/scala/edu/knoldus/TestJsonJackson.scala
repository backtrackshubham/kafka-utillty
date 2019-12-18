package edu.knoldus

import org.json4s.{JValue, _}
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write

case class BBox(lowerLeftX: Int,
                lowerLeftY: Int,
                upperRightX: Int,
                upperRightY: Int)

case class Object(bBox: BBox,
                  objId: Int,
                  objLabel: Int,
                  objLabelDefinition: String,
                  prob: Double)



object TestJsonJackson extends App {
  implicit val formats = DefaultFormats // Brings in default date formats etc.

  val json = """{"ImageData":{"imageFile":[],"imageEmpty":false,"imageId":"03707388-16a7-11ea-94a1-00044be64e82-00716","imageUUID":"03707388-16a7-11ea-94a1-00044be64e82","imagesDirUrl":"/kerb/images/e0c07a92-16a4-11ea-bbe2-00044be6503a/03707388-16a7-11ea-94a1-00044be64e82/03707388-16a7-11ea-94a1-00044be64e82-L","imageName":"03707388-16a7-11ea-94a1-00044be64e82-00716-L.jpg","unitId":"e0c07a92-16a4-11ea-bbe2-00044be6503a"},"imageObjects":[{"objId":0,"objLabel":0,"objLabelDefinition":"person","prob":0.905996561050415,"bBox":{"lowerLeftX":158,"lowerLeftY":282,"upperRightX":376,"upperRightY":358}},{"objId":1,"objLabel":58,"objLabelDefinition":"pottedplant","prob":0.8663430213928223,"bBox":{"lowerLeftX":296,"lowerLeftY":115,"upperRightX":334,"upperRightY":168}},{"objId":2,"objLabel":0,"objLabelDefinition":"person","prob":0.8485622406005859,"bBox":{"lowerLeftX":417,"lowerLeftY":101,"upperRightX":442,"upperRightY":189}},{"objId":5,"objLabel":56,"objLabelDefinition":"chair","prob":0.7766545414924622,"bBox":{"lowerLeftX":362,"lowerLeftY":203,"upperRightX":410,"upperRightY":282}},{"objId":4,"objLabel":56,"objLabelDefinition":"chair","prob":0.7520991563796997,"bBox":{"lowerLeftX":288,"lowerLeftY":190,"upperRightX":333,"upperRightY":267}},{"objId":6,"objLabel":56,"objLabelDefinition":"chair","prob":0.7287871837615967,"bBox":{"lowerLeftX":446,"lowerLeftY":226,"upperRightX":513,"upperRightY":317}},{"objId":7,"objLabel":56,"objLabelDefinition":"chair","prob":0.5736714005470276,"bBox":{"lowerLeftX":374,"lowerLeftY":171,"upperRightX":404,"upperRightY":182}},{"objId":3,"objLabel":0,"objLabelDefinition":"person","prob":0.5528091788291931,"bBox":{"lowerLeftX":215,"lowerLeftY":138,"upperRightX":262,"upperRightY":208}},{"objId":8,"objLabel":56,"objLabelDefinition":"chair","prob":0.5475171208381653,"bBox":{"lowerLeftX":526,"lowerLeftY":198,"upperRightX":563,"upperRightY":214}}],"objectDetectorId":"yolov3","timestamp":1575474586598}"""
  val jValue = parse(json)

  val newObjects = """[{"objId":0,"objLabel":0,"objLabelDefinition":"person","prob":0.905996561050415,"bBox":{"lowerLeftX":158,"lowerLeftY":282,"upperRightX":376,"upperRightY":358}},{"objId":1,"objLabel":58,"objLabelDefinition":"pottedplant","prob":0.8663430213928223,"bBox":{"lowerLeftX":296,"lowerLeftY":115,"upperRightX":334,"upperRightY":168}},{"objId":2,"objLabel":0,"objLabelDefinition":"person","prob":0.8485622406005859,"bBox":{"lowerLeftX":417,"lowerLeftY":101,"upperRightX":442,"upperRightY":189}},{"objId":5,"objLabel":56,"objLabelDefinition":"chair","prob":0.7766545414924622,"bBox":{"lowerLeftX":362,"lowerLeftY":203,"upperRightX":410,"upperRightY":282}},{"objId":4,"objLabel":56,"objLabelDefinition":"chair","prob":0.7520991563796997,"bBox":{"lowerLeftX":288,"lowerLeftY":190,"upperRightX":333,"upperRightY":267}},{"objId":6,"objLabel":56,"objLabelDefinition":"chair","prob":0.7287871837615967,"bBox":{"lowerLeftX":446,"lowerLeftY":226,"upperRightX":513,"upperRightY":317}},{"objId":7,"objLabel":56,"objLabelDefinition":"chair","prob":0.5736714005470276,"bBox":{"lowerLeftX":374,"lowerLeftY":171,"upperRightX":404,"upperRightY":182}},{"objId":3,"objLabel":0,"objLabelDefinition":"person","prob":0.5528091788291931,"bBox":{"lowerLeftX":215,"lowerLeftY":138,"upperRightX":262,"upperRightY":208}},{"objId":8,"objLabel":56,"objLabelDefinition":"chair","prob":0.5475171208381653,"bBox":{"lowerLeftX":526,"lowerLeftY":198,"upperRightX":563,"upperRightY":214}}]"""
//  val newList = (parse(newObjects).extract[List[Object]]) map (_.copy(distance = Some(56.456)))
//  println(newList)
//
//  println(jValue)
//  println(jValue \ "imageObjects")

  val updated = jValue transformField {
    case ("imageObjects", jvalues) =>
      println(jvalues)
      val finalImageObjects = jvalues match {
        case JsonAST.JArray(arr) =>
          arr map {jval =>
            jval merge render(parse("""{"distance" : 26.369}"""))
          }
      }



//
//      println("=====================================")
//      println(finalImageObject)
//      println(write(finalImageObject))
//      println("=====================================")
//
      ("imageObjects", JArray(finalImageObjects))
  }

  println(write(updated))

}
