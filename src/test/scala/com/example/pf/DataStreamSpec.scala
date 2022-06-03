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
    assert(ds != null)
    ds.push(Tensor(Array(0.0)), Tensor(Array(0.0)))
    ds.push(Tensor(Array(1.0)), Tensor(Array(1.0)))
    ds.push(Tensor(Array(2.0)), Tensor(Array(0.5)))
    assert(ds.dataStream == Tensor(Array(Array(0.0), Array(1.0), Array(0.5))))
    assert(ds.timeStream == Tensor(Array(Array(0.0), Array(1.0), Array(2.0))))
    var p0: Plot = null
    if enableX11 then
      val f = Figure("Particle filter(normal sd)")
      f.width = 1080
      p0 = f.subplot(0)
      //val p1 = f.subplot(1, 2, 1)
      p0.legend = true
      p0 += plot(ds.timeStream.toArray, ds.dataStream.toArray, name = "Input")
      f.refresh()

  }
  test("datastream should be tuned") {
      val ds = DataStream()
      assert(ds != null)
      ds.push(Tensor(Array(0.0)), Tensor(Array(0.0)))
      ds.push(Tensor(Array(1.0)), Tensor(Array(1.0)))
      ds.push(Tensor(Array(2.0)), Tensor(Array(4.0)))
      ds.push(Tensor(Array(3.0)), Tensor(Array(0.5)))
      assert(ds.dataStream == Tensor(Array(Array(0.0), Array(1.0), Array(4.0), Array(0.5))))
      assert(ds.timeStream == Tensor(Array(Array(0.0), Array(1.0), Array(2.0), Array(3.0))))
      ds.tuning
  }
