package com.example.pf.model
import com.example.pf.Tensor
import com.example.pf._
import com.example.pf.distribution.NormalDistribution

class LinearGaussianTrendSystemModel(mean: Double, sd: Double) extends SystemModel:
  val systemNoise = new NormalDistribution(mean, sd)
  // For the time being, an element of the x is assumed to be 2D.
  def systemModel(x: Tensor): Tensor =
    val m = x.length/2
    val v = x.map(f => systemNoise.sample()(0))
    //x + v
    val x1 = 2.0*x(0, m)-x(m, 2*m)+v(0, m)
    val x2 = x(0, m)
    x1 ++ x2


