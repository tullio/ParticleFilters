package com.example.pf

import breeze.plot.{Figure, Plot, plot}
import com.github.tototoshi.csv.{CSVReader, defaultCSVFormat}
import org.jfree.chart.annotations.XYTextAnnotation
import com.example.pf._
import com.example.pf.model._
import com.example.pf.distribution._

import scala.io.Source

object StockTrack:
  @main def StockTrace: Unit =
    val f = Figure("Stock Tracking")
    f.width = 1080
    f.height = 760

    val p0 = f.subplot(0)
    //val p1 = f.subplot(1, 2, 1)
    p0.legend = true
    p0.ylim(900, 3200)
    val isTuning = false

    val numParticle = 100
    if isTuning then
      var bestA = 0.0
      var bestB = 0.0
      var bestLikelihood = 0.0
      for
        a <- Range(50, 400, 50)
        b <- Range(50, 400, 50)
      do
        val csvResource = Source.fromResource("2181.csv")
        val reader = CSVReader.open(csvResource)
        val it = reader.iterator
        val header = it.next
        println(s"header info:${header}")

        val likelihood = execute(p0, it, numParticle, a, b)
        reader.close()
        if bestLikelihood > likelihood then
          bestLikelihood = likelihood
          bestA = a
          bestB = b
      print(s"results: likelihood = ${bestLikelihood}, a = ${bestA}, b = ${bestB}")
    else
      //val a = 300.0
      //val b = 350.0
      val a = 300.0
      val b = 100.0
      val csvResource = Source.fromResource("2181.csv")
      val reader = CSVReader.open(csvResource)
      val it = reader.iterator
      val header = it.next
      println(s"header info:${header}")

      val likelihood = execute(p0, it, numParticle, a, b)
      reader.close()


  def execute(p0: Plot, it: Iterator[Seq[String]], numParticle: Int, a: Double, b: Double) =

    //val systemModel = new LinearGaussianSystemModel(0.0, 100.0)
    val systemModel = new LinearGaussianTrendSystemModel(0.0, a)
    //val inversedObservationModel = new LinearGaussianObservationModel(0.0, 100.0)
    val inversedObservationModel = new LinearGaussianTrendObservationModel(0.0, b)
    val filter = new ParticleFilter(systemModel, inversedObservationModel)
    //var x = CauchyDistribution.fill(0.0, 1000.0, numParticle)
    val x1 = CauchyDistribution.fill(0.0, 1000.0, numParticle)
    val x2 = CauchyDistribution.fill(0.0, 1000.0, numParticle)
    var x = x1 ++ x2 // x = (x_n, x_{n-1})
    var index = 0.0
    var xBuffer = scala.collection.mutable.ListBuffer.empty[Double]
    var yBuffer = scala.collection.mutable.ListBuffer.empty[Double]
    var xIndexBuffer = scala.collection.mutable.ListBuffer.empty[Double]
    var yIndexBuffer = scala.collection.mutable.ListBuffer.empty[Double]
    var predictBuffer = scala.collection.mutable.ListBuffer.empty[Double]
    var predictIndexBuffer = scala.collection.mutable.ListBuffer.empty[Double]
    var likelihood = 0.0
    while it.hasNext do
      index += 1.0
      val line = it.next // 1 line in a CSV file
      val closeValue: Tensor = Tensor(Array(line(3).toDouble)) // 終値
      yBuffer.append(closeValue(0))
      yIndexBuffer.append(index)
      //val t = Tensor.repeat(index, x.length.toInt)
      val t = Tensor.repeat(index, x.length.toInt / 2)
      //x = if (index < 52.0) filter.step(x, closeValue) // x = (x_n, x_{n-1})
      x = if (index < 100.0) filter.step(x, closeValue) // x = (x_n, x_{n-1})
      else filter.step(x, closeValue, predictOnly = true) // x = (x_n, x_{n-1})
      //xBuffer.addAll(x.toArray)
      xBuffer.addAll(x(0, numParticle).toArray)
      xIndexBuffer.addAll(t.toArray)
      //val predict = x.mean(0)
      val predict = x(0, numParticle).mean(0)
      predictBuffer.append(predict)
      predictIndexBuffer.append(index)
      val l = filter.logLikelihood
      //println(s"logLikelyhood=${l}(a=${a}, b=${b})")
      likelihood += l
    p0 += plot(xIndexBuffer, xBuffer, name = "predict particle", style = '.')
    p0 += plot(yIndexBuffer, yBuffer, name = "input", style = '-', colorcode = "[255,0,0")
    p0 += plot(predictIndexBuffer, predictBuffer, name = "filter", style = '-', colorcode = "[0,255,0")
    println(s"xIndexBuffer.length=${xIndexBuffer.length}")
    //println(s"xIndexBuffer=${xIndexBuffer.toSeq}")
    println(s"xBuffer.length=${xBuffer.length}")
    //println(s"xBuffer=${xBuffer.toSeq}")
    println(s"logLikelyhood=${likelihood}(a=${a}, b=${b})")
    val text = s"logLikelyhood=${likelihood}(a=${a}, b=${b})"
    val annotation = new XYTextAnnotation(text, 500, 500)
    annotation.setFont(annotation.getFont().deriveFont(24f))
    p0.plot.addAnnotation(annotation)
    likelihood
