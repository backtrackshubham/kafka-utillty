package edu.knoldus
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object InterpolatorHelperV2 extends App {
//  (for{
//    _ <- FutureHelper.publishImageHeader
//    _ <- FutureHelper.publishGPSData
//  } yield true).onComplete{
//    case Success(value) => println("Completed")
//    case Failure(value) => println("Failed")
//  }

  FutureHelper.publishImageHeader
  FutureHelper.publishGPSData


  Thread.sleep(100000)
}
