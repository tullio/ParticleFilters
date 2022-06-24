package com.example.pf.model

import com.example.pf.Tensor
import com.example.pf.distribution.CauchyDistribution
import com.example.pf._
class
LinearCauchySystemModel(mean: Double, sd: Double) extends SystemModel:
  val systemNoise = new CauchyDistribution(mean, sd)
  def systemModel(x: Tensor): Tensor =
      val v = x.map(f => systemNoise.sample()(0))
      x + v
  override def toString() =
      s"LinearCauchySystemModel(${mean}, ${sd})"

