package com.example.pf

import org.nd4j.linalg.api.buffer.DataType
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.indexing.NDArrayIndex
import org.nd4j.linalg.indexing.conditions.EpsilonEquals

import scala.annotation.targetName
import scala.math
import scala.reflect.ClassTag

class Tensor:
  var x: INDArray = _
  class TensorItetator extends Iterator[Double]:
    private var current = 0
    override def hasNext: Boolean = current < x.length
    override def next(): Double =
      if hasNext then
        val t = x.getDouble(current.toLong)
        current += 1
        t
      else
        0.0
  def iterator =
    new TensorItetator

  @targetName("create_array")
  def create(in: Array[Double]) =
    x = Nd4j.create(in)
    this
  @targetName("create_seq")
  def create(in: Seq[Double]) =
    x = Nd4j.create(in.toArray)
    this
  def create(in: INDArray) =
    x = in.dup()
    this
  def create(shape: Int*) =
    x = Nd4j.create(shape*)
    this
  def cummulativeValues() =
    val n = x.length
    val cumulativeWeight = scala.collection.mutable.ListBuffer.empty[Double]
    cumulativeWeight.append(x.getDouble(0L))
    for
      i <- Range(1, n.toInt)
    do
      cumulativeWeight.append(cumulativeWeight(i - 1) + x.getDouble(i.toLong))
    Tensor(cumulativeWeight.toArray)
  def indexWhere(f: Double => Boolean) =
    x.toDoubleVector.indexWhere(f)
  def map(f: Double => Double) =
    Tensor(x.toDoubleVector.map(f))
  def filter(f: Double => Boolean) =
    Tensor(x.toDoubleVector.filter(f))
  def drop(n: Int) =
    Tensor(x.toDoubleVector.drop(n))
  def toArray =
    x.toDoubleVector
  def floor =
    Tensor(x.toDoubleVector.map(f => f.toInt.toDouble))
  def length: Long = x.length
  def histogram(nbin: Int, lower: Double, upper: Double): (Tensor, Tensor) =
    val Epsilon = 0.00001
    val sorted = x.toDoubleVector.sorted
    val min = math.min(sorted.head, lower)
    val max = math.max(sorted.last, upper)
    val binLength = (max - min) / nbin + Epsilon
    println("binLength="+binLength)
    val bins = Range(0, nbin).map(f => f*binLength + min)
    println("bins="+bins)
    val bucketsMap = sorted.map(f => ((f-min)/binLength).toInt).groupBy(f => f)
    val buckets = Range(0, nbin).map(f => if(bucketsMap.isDefinedAt(f)) bucketsMap(f).length else 1)
    (Tensor(bins.toArray), Tensor(buckets.toArray))
  override def toString(): String =
    var out = "["
    for
      i <- Range(0, x.length.toInt)
    do
      out = out + x.get(NDArrayIndex.point(i))
      if i < x.length.toInt - 1 then
        out = out + ", "
    out = out + "]"
    out
  def ==(y: Tensor)(implicit eps: Double = 1e-8): Boolean =
    x.equalsWithEps(y.x, eps)
  def /(y: Number): Tensor =
    Tensor(x.div(y))
  def mean =
    Tensor(Array(x.meanNumber().asInstanceOf[Double]))
  def sum =
    x.sumNumber().asInstanceOf[Double]
  def apply(a: Long, b: Long): Tensor = Tensor(x.get(NDArrayIndex.interval(a,b)))
  def apply(a: Int, b: Int): Tensor = Tensor(x.get(NDArrayIndex.interval(a,b)))
  def apply(i: Long) = x.getDouble(i)
  def apply(i: Int) = x.getDouble(i.toLong)

extension (x: Tensor)
  def +(y: Tensor): Tensor = Tensor(x.x.add(y.x))
  def ++(y: Tensor): Tensor = Tensor(Nd4j.hstack(x.x, y.x))
  def +(y: Number): Tensor = Tensor(x.x.add(y))
  def -(y: Tensor): Tensor = Tensor(x.x.sub(y.x))
  def -(y: Number): Tensor = Tensor(x.x.sub(y))
  def dot(y: Tensor): Tensor = Tensor(x.x.mmul(y.x))
  //def *(y: com.example.pf.Tensor): com.example.pf.Tensor = com.example.pf.Tensor(x.x.mul(y.x))
  def *(y: Number): Tensor = Tensor(x.x.mul(y))
  def norm: Double = x.x.norm2Number().doubleValue()

extension (x: Number)
  def *(y: Tensor): Tensor = y * x

given Tensor2Array: Conversion[Tensor, Array[Double]] with
  def apply(x: Tensor): Array[Double] = x.x.toDoubleVector


object Tensor:
  def apply(x: Array[Double]) =
    val t = new Tensor()
    t.create(x)
  def apply(x: Seq[Double]) =
    val t = new Tensor()
    t.create(x)
  def apply(x: Array[Int]) =
    val t = new Tensor()
    t.create(x.map(f => f.toDouble))
  def apply(x: INDArray) =
    val t = new Tensor()
    t.create(x)
  def create(shape: Int*) =
    val t = new Tensor()
    t.create(shape*)
  def linspace(start: Double, end: Double, step: Double = 1.0) =
    val num: Long = ((end - start)/step + 1).toLong
    val t = new Tensor()
    t.create(Nd4j.linspace(DataType.DOUBLE, start, step, num))
  def repeat(x: Double, n: Int) =
    val t = new Tensor()
    t.create(Nd4j.repeat(Nd4j.create(Array(x)), n).toDoubleVector)


