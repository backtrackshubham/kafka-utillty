package edu.knoldus

import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write
import net.liftweb.json._

case class Person(name: String, isMarried: Boolean, age: Int)

object JsonTester extends App{
  implicit val formats = DefaultFormats

  val jsonWithBool = """{"name" : "shubham", "isMarried" : true, "age" : 34}"""
  val jsonWithInt = """{"name" : "shubham", "isMarried" : 0, "age" : 34}"""
  val jsonWithIntStr = """{"name" : "shubham", "isMarried" : true, "age" : "34"}"""
  val jsonWithIntAge = """{"name" : "shubham", "isMarried" : true, "age" : 34}"""
  val jValueBool = parse(jsonWithBool)
  val jValueInt = parse(jsonWithInt)

  val jValueStrInt = parse(jsonWithIntStr)
  val jValueIntAge = parse(jsonWithIntAge)

  val boolOpt = jValueBool.extractOpt[Person]
  val boolInt = jValueInt.extractOpt[Person]
  val strInt = jValueStrInt.extractOpt[Person]
  val ageInt = jValueIntAge.extractOpt[Person]

  println("============================")
  println(boolOpt)
  println(boolInt)
  println(strInt)
  println(ageInt)

  println("============================")

}
