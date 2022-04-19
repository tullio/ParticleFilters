import org.scalatest.*
import flatspec.*
import matchers.*
import org.scalactic.*
import Tolerance.*
import org.scalatest.funsuite.AnyFunSuite
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.api.buffer.DataType
import org.nd4j.linalg.indexing.NDArrayIndex
import breeze.plot.*
import com.example.pf._
import com.example.pf.model._
import com.example.pf.distribution._
import org.jfree.chart.annotations.XYTextAnnotation

class FilterTest extends AnyFunSuite:
  test("Linear Filter Elements") {
    val x = Tensor(Array(1.0, 2.0))
    val v = Tensor(Array(3.0, 4.0))
    val systemModel = new LinearGaussianSystemModel(0.0, 1.0)
    val f = systemModel.systemModel(x)
    assert(f.==(Tensor(Array(2.055, 3.466)))(1e-3))
    val inversedObservationModel = new LinearGaussianObservationModel(0.0, 1.0)
    val g = inversedObservationModel.observationNoiseProbabirity(x, v)
    assert(g.==(Tensor(Array(0.053, 0.053)))(1e-3))
  }
  test("Linear Filter Run"){
    val x = Tensor(Array(1.0, 2.0))
    val systemModel = new LinearGaussianSystemModel(0.0, 1.0)
    val inversedObservationModel = new LinearGaussianObservationModel(0.0, 1.0)
    val systemNoise = new NormalDistribution(0.0, 1.0)
    val observationNoise = new NormalDistribution(0.0, 1.0)
    val systemNoiseParticle = Tensor(systemNoise.fill(2))
    println(s"w=${systemNoiseParticle}")
    assert(systemNoiseParticle.==(Tensor(Seq(1.0553, 1.4664)))(eps=1e-4))
    val f = x + systemNoiseParticle
    println(s"f=${f}")
    assert(f.==(Tensor(Seq(2.0553, 3.4664)))(eps=1e-4))
    val y = Tensor(Array(2.0, 2.5))
    val diff = inversedObservationModel.inversedObservationModel(y, f)
    println(s"v=${diff}")
    assert(diff.==(Tensor(Array(-0.0553, -0.9664)))(eps=1e-4))
    val a = observationNoise.density(diff)
    assert(a.==(Tensor(Array(0.3983, 0.2501)))(eps=1e-4))
    println(s"a=${a}")
    val resampling1 = Resampling()
    val g = resampling1.systematicResampling(f, a)
    println(g)
    assert(g.==(Tensor(Array(2.0553, 3.4664)))(eps=1e-4))
    val systemNoise2 = new NormalDistribution(0.0, 1.0)
    val observationNoise2 = new NormalDistribution(0.0, 1.0)
    val filter
    = new ParticleFilter(systemModel,
      inversedObservationModel)
    val newParticle = filter.step(x, y)
    assert(newParticle.==(g)(eps=1e-4))
    println(newParticle)
  }
  test("Steps 1") {
    val idx = Tensor(Array(0.0, 1.0, 2.0, 3.0))
    val ySeriese = Tensor(Array(1.0, 1.2, 2.0, 0.9))
    val f = Figure("Particle filter(normal sd)")
    f.width = 1080
    val p0 = f.subplot(0)
    //val p1 = f.subplot(1, 2, 1)
    p0.legend = true

    p0 += plot(idx.toArray, ySeriese.toArray, name = "Input")
    f.refresh()
    val systemModel = new LinearGaussianSystemModel(0.0, 1.0)
    val inversedObservationModel = new LinearGaussianObservationModel(0.0, 1.0)
    val systemNoise = new NormalDistribution(0.0, 1.0)
    val observationNoise = new NormalDistribution(0.0, 1.0)
    val filter
    = new ParticleFilter(systemModel,
      inversedObservationModel)
    var x = Tensor.repeat(ySeriese(0), 100)
    val i = idx.drop(1).iterator
    ySeriese.toArray.drop(1).foreach{f =>
      x = filter.step(x, Tensor(Array(f)))
      val t = Tensor.repeat(i.next(), x.length.toInt)
      p0 += plot(t.toArray, x.toArray, name = "predict", style='.')
    }
    f.refresh()


  }
  test("Steps 2") {
    val idx = Tensor(Array(0.0, 1.0, 2.0, 3.0))
    val ySeriese = Tensor(Array(1.0, 1.2, 2.0, 0.9))
    val f = Figure("Particle filter(small sd)")
    f.width = 1080
    val p0 = f.subplot(0)
    //val p1 = f.subplot(1, 2, 1)
    p0.legend = true

    p0 += plot(idx.toArray, ySeriese.toArray, name = "Input")
    f.refresh()
    val systemModel = new LinearGaussianSystemModel(0.0, 1.0)
    val inversedObservationModel = new LinearGaussianObservationModel(0.0, 0.2)
    val filter = new ParticleFilter(systemModel, inversedObservationModel)
    var x = Tensor.repeat(ySeriese(0), 100)
    val i = idx.drop(1).iterator
    ySeriese.toArray.drop(1).foreach{f =>
      x = filter.step(x, Tensor(Array(f)))
      val t0 = i.next()
      val t = Tensor.repeat(t0, x.length.toInt)
      p0 += plot(t.toArray, x.toArray, name = "predict particle", style='.')
      val predict = x.mean(0)
      p0 += plot(Array(t0), Array(predict), name = "predict value", style='+', colorcode="[255,0,0]")
      //println(s"mean=${predict}")
      val a = filter.logLikelihood
      println(s"logLikelyhood=${a}")

    }
    f.refresh()


  }
  test("Parameter Tuning") {
    val idx = Tensor(Array(0.0, 1.0, 2.0, 3.0))
    val ySeriese = Tensor(Array(1.0, 1.2, 2.0, 0.9))
    //f.refresh()
    for
      i <- Range(0, 3, 2)
      j <- Range(0, 3, 2)
    do
      val a = math.pow(2.0, -i)
      val b = math.pow(2.0, -j)
      val f = Figure(s"Particle filter(a=${a}, b=${b})")
      f.width = 1080
      val p0 = f.subplot(0)
      //val p1 = f.subplot(1, 2, 1)
      p0.legend = true
      p0 += plot(idx.toArray, ySeriese.toArray, name = "Input")
      val systemModel = new LinearGaussianSystemModel(0.0, a)
      val inversedObservationModel = new LinearGaussianObservationModel(0.0, b)
      val filter = new ParticleFilter(systemModel, inversedObservationModel)
      var x = Tensor.repeat(ySeriese(0), 100)
      val it = idx.drop(1).iterator
      var likelihood = 0.0
      ySeriese.toArray.drop(1).foreach { f =>
        x = filter.step(x, Tensor(Array(f)))
        val t0 = it.next()
        val t = Tensor.repeat(t0, x.length.toInt)
        p0 += plot(t.toArray, x.toArray, name = "predict particle", style = '.')
        val predict = x.mean(0)
        p0 += plot(Array(t0), Array(predict), name = "predict value", style = '+', colorcode = "[255,0,0]")
        //println(s"mean=${predict}")
        val l = filter.logLikelihood
        //println(s"logLikelyhood=${l}(a=${a}, b=${b})")
        likelihood += l
      }
      println(s"logLikelyhood=${likelihood}(a=${a}, b=${b})")
      val text = s"logLikelyhood=${likelihood}(a=${a}, b=${b})"
      val annotation = new XYTextAnnotation(text, 500, 500)
      annotation.setFont(annotation.getFont().deriveFont(24f))
      p0.plot.addAnnotation(annotation)
      //f.refresh()
  }