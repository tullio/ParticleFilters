package com.example.pf

import scala.util.Random
class Resampling(seed: Int = 0):
  val rnd = new Random(seed)
  def multinomialResampling(x: Tensor, w: Tensor) =
    //println("input="+w)
    val total = w.sum
    //println("total="+total)
    val n = w.length
    //println("cummulativeValues="+w.cummulativeValues())
    val cumulativeWeight = w.cummulativeValues()/total
    //println(f"cumulativeWeight=${cumulativeWeight}")
    val samplingMap = x.map{
      f =>
        val r = Random.nextDouble()
        val index = cumulativeWeight.indexWhere(g => g > r)
        index
    }
    //println("samplingMap="+samplingMap)
    val p = samplingMap.map{
      f => x(f.toLong)
    }
    p
  def systematicResampling(x: Tensor, w: Tensor) =
    val total = w.sum
    val n = w.length
    val cumulativeWeight = w.cummulativeValues()/total
    //println(f"cumulativeWeight=${cumulativeWeight}")
    val r0 = rnd.nextDouble() * 1.0/n
    val samplingMap = Tensor(Range(0, n.toInt).toArray).map{
      f =>
        val r = r0 + f/n
        val index = cumulativeWeight.indexWhere(g => g >= r)
        index
    }
    val p = samplingMap.map{
      f => x(f.toLong)
    }
    p
  def residualResampling(x: Tensor, w: Tensor) =
    val total = w.sum
    val n = w.length
    val normalizedWeight = w.map(f => f/total)
    //println(s"normalized weights = ${normalizedWeight}")
    val replicationFactor = (normalizedWeight * n).floor
    //println(s"replication factors = ${replicationFactor}")
    val modifiedWeight = normalizedWeight - replicationFactor/n
    //println(s"modified weights = ${modifiedWeight}")
    val temporalLength = replicationFactor.sum
    //println(s"sum of replication factor = ${temporalLength}")
    var samplingMap = scala.collection.mutable.ListBuffer.empty[Int]
    val replicationMap = replicationFactor.toArray.zipWithIndex.filter(f => f._1>0.0)
    //println(s"replicationMap = ${replicationMap.toSeq}")
    replicationMap.foreach{f =>
      Range(0, f._1.toInt /* replication factor */).foreach{g =>
        samplingMap.append(f._2 /* index */)
      }
    }
    //println(s"1st samplingMap = ${samplingMap.toSeq}")
    val p = Tensor(samplingMap.map{
      f => x(f.toLong)
    }.toArray)
    //println(s"1st results = ${p}")
    val (residualX, residualWeight) = x.toArray.zip(modifiedWeight.toArray).filter(f => f._2 > 0.0)
      .sortBy(f => f._2)
      .take((n-temporalLength).toInt)
      .unzip
    //println(s"residual x = ${residualX.toSeq}")
    //println(s"residual weights = ${residualWeight.toSeq}")
    val r = multinomialResampling(Tensor(residualX), Tensor(residualWeight))
    //println(s"2nd results = ${r}")
    //println(s"final results = ${p ++ r}")
    p ++ r
  def stratifiedResampling(x: Tensor, w: Tensor) =
    val total = w.sum
    val n = w.length
    val cumulativeWeight = w.cummulativeValues()/total
    //println(f"cumulativeWeight=${cumulativeWeight}")
      val samplingMap = Tensor(Range(0, n.toInt).toArray).map{
      f =>
        val r0 = rnd.nextDouble() * 1.0/n
        val r = r0 + f/n
        val index = cumulativeWeight.indexWhere(g => g >= r)
        index
    }
    val p = samplingMap.map{
      f => x(f.toLong)
    }
    p
object Resampling:
  val resampling = Resampling()
  def multinomialResampling(x: Tensor, w: Tensor) =
    resampling.multinomialResampling(x, w)
  def systematicResampling(x: Tensor, w: Tensor) =
    resampling.systematicResampling(x, w)
  def residualResampling(x: Tensor, w: Tensor) =
    resampling.residualResampling(x, w)
  def stratifiedResampling(x: Tensor, w: Tensor) =
    resampling.stratifiedResampling(x, w)




