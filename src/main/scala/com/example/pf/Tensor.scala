package com.example.pf

import org.nd4j.linalg.api.buffer.DataType
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.indexing.NDArrayIndex
import org.nd4j.linalg.indexing.conditions.EpsilonEquals
import org.tinylog.Logger
import scala.annotation.targetName
import scala.math
import scala.reflect.ClassTag

class Tensor:
  var x: INDArray = _
  var outputNewLine = true
  def doubleIterator =
      class DoubleItetator extends Iterator[Double]:
        private var current = 0
        override def hasNext: Boolean = current < x.length
        override def next(): Double =
          if hasNext then
            val t = x.getDouble(current.toLong)
            current += 1
            t
          else
            0.0
      new DoubleItetator
  def tensorIterator =
      class TensorItetator extends Iterator[Tensor]:
        private var current = 0L
        private var currentTensor = Tensor(x.getRow(current))
        def value = x
        override def hasNext: Boolean = 
          //println(s"current=${current}, length=${x.shape()(0)}")
          current < x.shape()(0)
        override def next(): Tensor =
          if hasNext then
            currentTensor.x = x.getRow(current.toLong)
            current += 1
            //println(s"return=${currentTensor}")
            currentTensor
          else
            currentTensor
      new TensorItetator

  // 1D ------------------------------------
  @targetName("create_from_varargs")
  def create(in: Double*) =
    x = Nd4j.create(in.toArray)
    this

  @targetName("create_array")
  def create(in: Array[Double]) =
    x = Nd4j.create(in)
    this
  def create(in: Array[Array[Double]]) =
    x = Nd4j.create(in)
    this
  @targetName("create_seq")
  def create(in: Seq[Double]) =
    x = Nd4j.create(in.toArray)
    this
  def create(in: INDArray) =
    x = in.dup()
    this
  @targetName("create_by_shape")
  def create(in: Seq[Seq[Double]]) =
    //println(s"in=${in}")
    x = Nd4j.create(in.map(f => f.toArray).toArray)
    //println(s"x=${x}")
    this
  def create(shape: Int*) =
    //x = Nd4j.create(shape*, DataType.DOUBLE)
    x = Nd4j.create(shape.toArray, DataType.DOUBLE)
    this

  def shape =
    x.shape

  def reshape(shape: Int*) =
    x = x.reshape(shape.toArray)
    this
  /**
   * In 1-dimensional case, an input 1-D tensor is appended to the original
   * 1-D tensor. In 2-dimensional case, an input 1-D tensor is appended to the
   * original 2-D tensor.
    * The context "x" is replaced.
   * @param in Tensor to be appended
   * @return this
   */
  def push(in: Tensor) =
    //println(s"push ${x}(${x.shape.toSeq}; ${x.dataType}) into ${in.x}(${in.x.shape.toSeq}; ${in.x.dataType})")
    x = x.shape.length match

      case 1 =>
        //println(s"push ${x} into ${in.x}")
        Nd4j.hstack(x, in.x)
      case 2 =>
        Nd4j.vstack(x, in.x.reshape(1, in.x.length))

    this
  /**
    * The context "x" is replaced.
    * @param in Array represents 1xlength Tensor
    * @return this
    * */
  def push(in: Array[Double]) =
      x = Nd4j.vstack(x, Tensor(in).x.reshape(in.length, 1))
      this
  /**
    * An input parameter is regarded as a series of 1-D tensor.
    * The context "x" is replaced.
    * */
  def multiplePush(in: Tensor) =
      x = Nd4j.vstack(x, in.x.reshape(in.x.length, 1))
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

  // for 1D and 2D
  def map(f: Double => Double) =
    x.shape.length match
        case 1 =>
            Tensor(Nd4j.create(x.toDoubleVector.map(f)))
        case 2 =>
            Tensor(Nd4j.create(x.toDoubleMatrix.map(g => g.map(f))))



  def filter(f: Double => Boolean) =
    Tensor(x.toDoubleVector.filter(f))
  def drop(n: Int) =
    Tensor(x.toDoubleVector.drop(n))
  def take(n: Int) =
    Tensor(x.toDoubleVector.take(n))

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
    //println("binLength="+binLength)
    val bins = Range(0, nbin).map(f => f*binLength + min)
    //println("bins="+bins)
    val bucketsMap = sorted.map(f => ((f-min)/binLength).toInt).groupBy(f => f)
    val buckets = Range(0, nbin).map(f => if(bucketsMap.isDefinedAt(f)) bucketsMap(f).length else 1)
    (Tensor(bins.toArray), Tensor(buckets.toArray))
  override def toString(): String =
    val shape = x.shape()
    var out = "["
    shape.length match
      case 1 =>
        for
          i <- Range(0, x.length.toInt)
        do
          out = out + x.get(NDArrayIndex.point(i))
          if i < x.length.toInt - 1 then
            out = out + ", "

      case 2 =>
        for
          i <- Range(0, shape(0).toInt)
        do
          out = out + "["
          for
            j <- Range(0, shape(1).toInt)
          do
            out = out + x.get(NDArrayIndex.point(i), NDArrayIndex.point(j))
            if j < shape(1) - 1 then
              out = out + ", "
          out = out + "]"
          if i < shape(0) - 1 then
            out = out + ", "
            if outputNewLine then
              out = out + "\n"
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
  def update(idx: Int, v: Double) =
      x.putScalar(idx.toLong, v)
  def getColumn(i: Int) = Tensor(x.getColumn(i))
  def getRow(i: Int) = Tensor(x.getRow(i.toLong))
  def dup = Tensor(x)

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
  // 1D --------------------------------
  def apply(x: Array[Double]) =
    val t = new Tensor()
    t.create(x)
  // 2D --------------------------------
  def apply(x: Array[Array[Double]]) =
    val t = new Tensor()
    t.create(x)

  @targetName("apply_from_varargs")
  def apply(x: Double*) =
    val t = new Tensor()
    t.create(x)

  @targetName("apply_from_seq")
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
  // 2D --------------------------------
  def apply(x: Seq[Seq[Double]]) =
    val t = new Tensor()
    t.create(x)
  def linspace(start: Double, end: Double, step: Double = 1.0) =
    val num: Long = ((end - start)/step + 1).toLong
    val t = new Tensor()
    t.create(Nd4j.linspace(DataType.DOUBLE, start, step, num))
  def repeat(x: Double, n: Int) =
    val t = new Tensor()
    t.create(Nd4j.repeat(Nd4j.create(Array(x)), n).toDoubleVector)
  def repeat(x: Tensor, n: Int) =
    val t = new Tensor()
    t.create(Nd4j.repeat(x.x, n))

  def empty =
    new Tensor()



