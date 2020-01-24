package edu.knoldus

import java.io.File
import java.nio.file.Files

import edu.knoldus.model.ImageHeaderData
import edu.knoldus.producer.DataProducer
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object FutureHelper {
  implicit val formats: DefaultFormats.type = DefaultFormats

  val currentPath: String = System.getProperty("user.dir")
  val imagePath = s"resources/images/fifty-kb/"
  val fileList: List[File] = List(new File(s"${imagePath}beginingEnd.jpg"),
    new File(s"${imagePath}karma.jpg"),
    new File(s"${imagePath}moksha.jpg"),
    new File(s"${imagePath}withoutExpectations.jpg"),
    new File(s"${imagePath}noJudgement.jpg"),
    new File(s"${imagePath}worldWhatYouImagine.jpg"),
    new File(s"${imagePath}constantChnage.jpg"),
    new File(s"${imagePath}realUnreal.jpg"),
    new File(s"${imagePath}goodEvil.jpg"),
    new File(s"${imagePath}universe.jpg"))
}
