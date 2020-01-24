package edu.knoldus

import java.io.InputStream

import net.liftweb.json.DefaultFormats


object FutureHelper {
  implicit val formats: DefaultFormats.type = DefaultFormats
  val imagePath = s"images/fifty-kb/"

  val lam: (String, String) => InputStream = (path: String, fileName: String) => {
    val fileInJar = getClass().getResourceAsStream(s"/$path$fileName")
    fileInJar
  }
  val fileNames: String => InputStream = lam(imagePath, _)
  val files = List("beginingEnd.jpg",
    "karma.jpg",
    "moksha.jpg",
    "withoutExpectations.jpg",
    "noJudgement.jpg",
    "worldWhatYouImagine.jpg",
    "constantChnage.jpg",
    "realUnreal.jpg",
    "goodEvil.jpg",
    "universe.jpg")

  val functionalFileList: List[InputStream] = files.map(fileNames)

}
