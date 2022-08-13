package com.example.pf
import breeze.plot.{Figure, Plot, plot}
import com.example.pf.model.{LinearGaussianObservationModel, LinearGaussianSystemModel}
import com.example.pf.Tensor
import org.scalactic.*
import org.scalactic.Tolerance.*
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.*


class DataStreamSpec extends AnyFunSuite with gui:
  test("datastream should be constructed") {
    val systemModel = new LinearGaussianSystemModel(0.0, 0.1)
    val inversedObservationModel = new LinearGaussianObservationModel(0.0,0.1)
    val filter = ParticleFilter(systemModel, inversedObservationModel)
    val ds = DataStream()
    ds.enableX11 = true
    assert(ds != null)
    ds.timeDataStream.push(Tensor(Array(0.0)), Tensor(Array(0.0)))
    ds.timeDataStream.push(Tensor(Array(1.0)), Tensor(Array(1.0)))
    ds.timeDataStream.push(Tensor(Array(2.0)), Tensor(Array(0.5)))
    assert(ds.timeDataStream.data == Tensor(Array(Array(0.0), Array(1.0), Array(0.5))))
    assert(ds.timeDataStream.time == Tensor(Array(Array(0.0), Array(1.0), Array(2.0))))

    ds.timeDataStream.multiplePush(Tensor(3.0, 4.0),Tensor(2.0, 2.5))
    assert(ds.timeDataStream.data == Tensor(Array(Array(0.0), Array(1.0), Array(0.5), Array(2.0), Array(2.5))))
    assert(ds.timeDataStream.time == Tensor(Array(Array(0.0), Array(1.0), Array(2.0), Array(3.0), Array(4.0))))
    enableX11 = true

    var p0: Plot = null
    if enableX11 then
      val f = Figure("Particle filter(normal sd)")
      f.width = 1080
      p0 = f.subplot(0)
      //val p1 = f.subplot(1, 2, 1)
      p0.legend = true
      p0 += plot(ds.timeDataStream.time.toArray, ds.timeDataStream.data.toArray, name = "Input")
      f.refresh()

  }
  test("datastream should be tuned") {
      val ds = DataStream()
      ds.enableX11 = false
      assert(ds != null)
      ds.timeDataStream.push(Tensor(Array(0.0)), Tensor(Array(0.0)))
      ds.timeDataStream.push(Tensor(Array(1.0)), Tensor(Array(1.0)))
      ds.timeDataStream.push(Tensor(Array(2.0)), Tensor(Array(4.0)))
      ds.timeDataStream.push(Tensor(Array(3.0)), Tensor(Array(0.5)))
      assert(ds.timeDataStream.data == Tensor(Array(Array(0.0), Array(1.0), Array(4.0), Array(0.5))))
      assert(ds.timeDataStream.time == Tensor(Array(Array(0.0), Array(1.0), Array(2.0), Array(3.0))))
      ds.completeTimeSeries = Tensor(Array(0.0, 1.0, 2.0, 3.0))
      ds.tuning
  }
  test("sampling should be performed") {
      var ds = DataStream()
      ds.enableX11 = false
      assert(ds != null)
      ds.timePredictStream.push(Tensor(Array(0.0)), Tensor(Array(0.0)))
      ds.timePredictStream.push(Tensor(Array(1.0)), Tensor(Array(1.0)))
      ds.timePredictStream.push(Tensor(Array(2.0)), Tensor(Array(4.0)))
      ds.timePredictStream.push(Tensor(Array(3.0)), Tensor(Array(0.5)))
      ds.timePredictStream.push(Tensor(Array(4.0)), Tensor(Array(0.4)))
      var timeSampleStream = ds.timePredictStreamSampling(2)
      //println(ds.timePredictStream)
      //println(ds.timeSampleStream)
      assert(timeSampleStream.time == Tensor(Array(Array(0.0), Array(2.0), Array(4.0))))
      assert(timeSampleStream.data == Tensor(Array(Array(1.0, 0.0), Array(4.0, 0.5), Array(0.4, 0.4))))
      ds = DataStream()
      ds.timePredictStream.push(Tensor(Array(0.0)), Tensor(Array(0.0)))
      ds.timePredictStream.push(Tensor(Array(1.0)), Tensor(Array(1.0)))
      ds.timePredictStream.push(Tensor(Array(2.0)), Tensor(Array(4.0)))
      ds.timePredictStream.push(Tensor(Array(3.0)), Tensor(Array(0.5)))
      ds.timePredictStream.push(Tensor(Array(4.0)), Tensor(Array(0.4)))
      ds.timePredictStream.push(Tensor(Array(5.0)), Tensor(Array(0.3)))
      timeSampleStream = ds.timePredictStreamSampling(2)
      //println(ds.timePredictStream)
      //println(ds.timeSampleStream)
      assert(timeSampleStream.time == Tensor(Array(Array(0.0), Array(2.0), Array(4.0))))
      assert(timeSampleStream.data == Tensor(Array(Array(1.0, 0.0), Array(4.0, 0.5), Array(0.4, 0.3))))
      ds = DataStream()
      ds.timePredictStream.push(Tensor(Array(0.0)), Tensor(Array(0.0)))
      ds.timePredictStream.push(Tensor(Array(1.0)), Tensor(Array(1.0)))
      ds.timePredictStream.push(Tensor(Array(2.0)), Tensor(Array(4.0)))
      ds.timePredictStream.push(Tensor(Array(3.0)), Tensor(Array(0.5)))
      ds.timePredictStream.push(Tensor(Array(4.0)), Tensor(Array(0.4)))
      timeSampleStream = ds.timePredictStreamSampling(3)
      //println("---")
      //println(ds.timePredictStream)
      //println("---")
      //println(ds.timeSampleStream)
      assert(timeSampleStream.time == Tensor(Array(Array(0.0), Array(3.0))))
      assert(timeSampleStream.data == Tensor(Array(Array(4.0, 0.0), Array(0.5, 0.4))))
      ds = DataStream()
      ds.timePredictStream.push(Tensor(Array(0.0)), Tensor(Array(0.0)))
      ds.timePredictStream.push(Tensor(Array(2.0)), Tensor(Array(4.0)))
      ds.timePredictStream.push(Tensor(Array(3.0)), Tensor(Array(0.5)))
      ds.timePredictStream.push(Tensor(Array(4.0)), Tensor(Array(0.4)))
      timeSampleStream = ds.timePredictStreamSampling(3)
      //println("---")
      //println(ds.timePredictStream)
      //println("---")
      //println(ds.timeSampleStream)
      assert(timeSampleStream.time == Tensor(Array(Array(0.0), Array(3.0))))
      assert(timeSampleStream.data == Tensor(Array(Array(4.0, 0.0), Array(0.5, 0.4))))
      ds = DataStream()
      ds.timePredictStream.push(Tensor(Array(0.0)), Tensor(Array(0.0)))
      ds.timePredictStream.push(Tensor(Array(2.0)), Tensor(Array(4.0)))
      ds.timePredictStream.push(Tensor(Array(4.0)), Tensor(Array(0.4)))
      timeSampleStream = ds.timePredictStreamSampling(3)
      //println("---")
      //println(ds.timePredictStream)
      //println("---")
      //println(ds.timeSampleStream)
      assert(timeSampleStream.time == Tensor(Array(Array(0.0), Array(4.0))))
      assert(timeSampleStream.data == Tensor(Array(Array(4.0, 0.0), Array(0.4, 0.4))))
  }
