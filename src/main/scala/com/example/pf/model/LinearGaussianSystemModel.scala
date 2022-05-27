package com.example.pf.model
import com.example.pf.Tensor
import com.example.pf.distribution.NormalDistribution
import com.example.pf._
class
LinearGaussianSystemModel(mean: Double, sd: Double) extends SystemModel:
  val systemNoise = new NormalDistribution(mean, sd)
  def systemModel(x: Tensor): Tensor =
    val v = x.map(f => systemNoise.sample()(0))
    x + v


