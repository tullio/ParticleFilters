package com.example.pf.distribution


import com.example.pf.Tensor
import org.nd4j.linalg.api.rng.distribution.BaseDistribution as JavaBaseDistribution

class BaseDistribution():
  var dist: JavaBaseDistribution = _
  def density(x: Double): Double =
    dist.density(x)
  def density(x: Tensor): Tensor =
    // How to tell element-wise operation and tensor operation
    // An idea is: the base function is that the element-wise operation like pytorch.tensor,
    // and the method name for the tensor operation changes from the element-wise operation's one.
    //Tensor(x.map(f => dist.density(f)))
    x.map(f => dist.density(f))
  def density2(x: Tensor): Double =
    dist.density(x(0)) // Multivariate version is not available
  def sample(): Tensor =
    Tensor(Array(dist.sample())) // Todo: Multivariate version is not available
  def fill(n: Int): Seq[Double] =
    Seq.fill[Double](n)(dist.sample())


