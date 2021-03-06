package com.lightbend.recommender.client.client

import java.io.ByteArrayOutputStream
import com.lightbend.model.recommendationrequest.RecommendationRequest
import com.lightbend.recommender.ModelServingConfiguration
import com.lightbend.recommender.client.MessageSender

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

/**
 * Application publishing models from /data directory to Kafka.
 */
object DataProviderCloud {

  import ModelServingConfiguration._

  val generator = Random
  val dataTimeInterval = Duration(DATAPUBLISHINTERVAL)


  def main(args: Array[String]) {

    println("Recommender data provider")
    println(s"Using kafka brokers at $KAFKA_BROKER")
    println(s"Using kafka topic $DATA_TOPIC")
    println(s"Data Message delay $dataTimeInterval")

    publishData()

    while(true)
      pause(600000)
  }

  def publishData() : Future[Unit] = Future {

    val sender = MessageSender(KAFKA_BROKER)
    val bos = new ByteArrayOutputStream()
    var nrec = 0
    while (true) {
      val user = generator.nextInt(1000).toLong
      val nprods = generator.nextInt(30)
      var products = new ListBuffer[Long]()
      0 to nprods foreach { _ => products += generator.nextInt(300).toLong}
      val data = new RecommendationRequest(products, user)
      bos.reset()
      data.writeTo(bos)
      sender.writeValue(DATA_TOPIC, bos.toByteArray)
      nrec = nrec + 1
      if (nrec % 10 == 0)
        println(s"wrote $nrec records")
      pause(dataTimeInterval.toMillis)
    }
  }


  private def pause(timeInterval : Long): Unit = {
    try {
      Thread.sleep(timeInterval)
    } catch {
      case _: Throwable => // Ignore
    }
  }
}
