name := "kerb-kafka-publisher"

version := "0.1"

scalaVersion := "2.12.0"

val kafka = "org.apache.kafka" % "kafka-clients" % "2.3.0"

libraryDependencies += "net.liftweb" %% "lift-json" % "3.2.0-M3"

libraryDependencies ++= Seq(kafka)

