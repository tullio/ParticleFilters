package com.example.pf.model
import com.example.pf.Tensor
import com.example.pf.distribution.NormalDistribution
import com.example.pf._
class
LinearGaussianSystemModel(mean: Double, sd: Double) extends SystemModel:
  val systemNoise = new NormalDistribution(mean, sd)
  def systemModel(x: Tensor): Tensor =
      //val v = x.map(f => systemNoise.sample()(0)) //  Run completed in 43 seconds
      val v = systemNoise.sample(x.shape) // Run completed in 23 seconds
      //val v = Range(0, x.length.toInt).map(f => systemNoise.sample()(0)) //  Run completed in 12 seconds -> xのshapeと違うものを作るのでダメ
      x + v
  override def toString() =
      s"LinearGaussianSystemModel(${mean}, ${sd})"

