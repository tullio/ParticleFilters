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
import com.example.pf.distribution._
class ResamplingTest extends AnyFunSuite:
  test("Multinomial com.example.pf.Resampling"){
    val idx = Tensor.linspace(-2, 2, 0.001)
    val udist = new UniformDistribution(-2, 2)
    val x = Tensor(idx.toArray.map(f => udist.sample()(0)).sortWith((f, g) => f < g))
    val (histidx, xhist) = x.histogram(40, -2, 2)
    //println(s"idx.length=${idx.toArray.length}")
    //println(s"xhist.length=${xhist.toArray.length}")
    //println(s"histidx.length=${histidx.toArray.length}")
    val ndist = new NormalDistribution(0.0, 1.0)
    val w = ndist.density(idx)
    val resampled = Resampling.multinomialResampling(x, w)
    val (reidx, rehist) = resampled.histogram(40, -2, 2)
    val f = Figure("Multinomial com.example.pf.Resampling")
    f.width= 1080

    val p0 = f.subplot(0)

    val p1 = f.subplot(1, 2, 1)
    //println(s"index=${idx.toArray.toSeq}")
    //println(s"input=${x.toArray.toSeq}")
    //println(s"probability density=${w.toArray.toSeq}")
    // println(s"resampled values = ${resampled.toArray.toSeq}")
    p0.legend = true
    p1.legend = true
    p0 += plot(idx.toArray, w.toArray, name="probability density")
    p0 += plot(idx.toArray, x.toArray, name="input")
    p1 += plot(histidx.toArray, xhist.toArray, name="input hist", style='+')
    p1 += plot(reidx.toArray, rehist.toArray, name="resampled hist", style='+')
    p0 += plot(idx.toArray, resampled.toArray,name="resampling values")
    f.refresh()
  }
  test("Systematic com.example.pf.Resampling"){
    val idx = Tensor.linspace(-2, 2, 0.001)
    val udist = new UniformDistribution(-2, 2)
    val x = Tensor(idx.toArray.map(f => udist.sample()(0)).sortWith((f, g) => f < g))
    val (histidx, xhist) = x.histogram(40, -2, 2)
    //println(s"idx.length=${idx.toArray.length}")
    //println(s"xhist.length=${xhist.toArray.length}")
    //println(s"histidx.length=${histidx.toArray.length}")
    val ndist = new NormalDistribution(0.0, 1.0)
    val w = ndist.density(idx)
    val resampled = Resampling.systematicResampling(x, w)
    val (reidx, rehist) = resampled.histogram(40, -2, 2)
    val f = Figure("Systematic com.example.pf.Resampling")
    f.width= 1080
    val p0 = f.subplot(0)
    val p1 = f.subplot(1, 2, 1)
    //println(s"index=${idx.toArray.toSeq}")
    //println(s"input=${x.toArray.toSeq}")
    //println(s"probability density=${w.toArray.toSeq}")
    //println(s"resampled values = ${resampled.toArray.toSeq}")
    p0.legend = true
    p1.legend = true
    p0 += plot(idx.toArray, w.toArray, name="probability density")
    p0 += plot(idx.toArray, x.toArray, name="input")
    p1 += plot(histidx.toArray, xhist.toArray, name="input hist", style='+')
    p1 += plot(reidx.toArray, rehist.toArray, name="resampled hist", style='+')
    p0 += plot(idx.toArray, resampled.toArray,name="resampling values")
    f.refresh()
  }
  test("ResidualResampling"){
    val idx = Tensor.linspace(-2, 2, 0.001)
    val udist = new UniformDistribution(-2, 2)
    val x = Tensor(idx.toArray.map(f => udist.sample()(0)).sortWith((f, g) => f < g))
    val (histidx, xhist) = x.histogram(40, -2, 2)
    println(s"idx.length=${idx.toArray.length}")
    println(s"xhist.length=${xhist.toArray.length}")
    println(s"histidx.length=${histidx.toArray.length}")
    val ndist = new NormalDistribution(0.0, 0.1)
    val w = ndist.density(idx)
    val resampled = Resampling.residualResampling(x, w)
    val (reidx, rehist) = resampled.histogram(40, -2, 2)
    val f = Figure("Residual com.example.pf.Resampling")
    f.width= 1080
    val p0 = f.subplot(0)
    val p1 = f.subplot(1, 2, 1)
    println(s"index=${idx.toArray.toSeq}")
    println(s"input=${x.toArray.toSeq}")
    println(s"probability density=${w.toArray.toSeq}")
    println(s"resampled values = ${resampled.toArray.toSeq}")
    p0.legend = true
    p1.legend = true
    p0 += plot(idx.toArray, w.toArray, name="probability density")
    p0 += plot(idx.toArray, x.toArray, name="input")
    p1 += plot(histidx.toArray, xhist.toArray, name="input hist", style='+')
    p1 += plot(reidx.toArray, rehist.toArray, name="resampled hist", style='+')
    p0 += plot(idx.toArray, resampled.toArray,name="resampling values")
    f.refresh()
  }
  test("Stratified com.example.pf.Resampling"){
    val idx = Tensor.linspace(-2, 2, 0.001)
    val udist = new UniformDistribution(-2, 2)
    val x = Tensor(idx.toArray.map(f => udist.sample()(0)).sortWith((f, g) => f < g))
    val (histidx, xhist) = x.histogram(40, -2, 2)
    //println(s"idx.length=${idx.toArray.length}")
    //println(s"xhist.length=${xhist.toArray.length}")
    //println(s"histidx.length=${histidx.toArray.length}")
    val ndist = new NormalDistribution(0.0, 1.0)
    val w = ndist.density(idx)
    val resampled = Resampling.systematicResampling(x, w)
    val (reidx, rehist) = resampled.histogram(40, -2, 2)
    val f = Figure("Stratified com.example.pf.Resampling")
    f.width= 1080
    val p0 = f.subplot(0)
    val p1 = f.subplot(1, 2, 1)
    //println(s"index=${idx.toArray.toSeq}")
    //println(s"input=${x.toArray.toSeq}")
    //println(s"probability density=${w.toArray.toSeq}")
    //println(s"resampled values = ${resampled.toArray.toSeq}")
    p0.legend = true
    p1.legend = true
    p0 += plot(idx.toArray, w.toArray, name="probability density")
    p0 += plot(idx.toArray, x.toArray, name="input")
    p1 += plot(histidx.toArray, xhist.toArray, name="input hist", style='+')
    p1 += plot(reidx.toArray, rehist.toArray, name="resampled hist", style='+')
    p0 += plot(idx.toArray, resampled.toArray,name="resampling values")
    f.refresh()
  }
