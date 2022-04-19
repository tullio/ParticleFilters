package com.example.pf.distribution

import com.example.pf.Tensor
import org.nd4j.linalg.api.rng.DefaultRandom
import org.nd4j.linalg.api.rng.distribution.impl.UniformDistribution
class CauchyDistribution(seed: Int, mean: Double, sd: Double) extends BaseDistribution:
  val rnd = new DefaultRandom(seed.toLong)
  override val dist = new UniformDistribution(rnd, 0, 1)
  def this(mean: Double, sd: Double) =
    this(0, mean, sd)
  override def density(x: Double): Double =
    1.0/(math.Pi*(math.pow((x-mean)/sd, 2.0)+1))
  override def density(x: Tensor): Tensor =
  // How to tell element-wise operation and tensor operation
  // An idea is: the base function is that the element-wise operation like pytorch.tensor,
  // and the method name for the tensor operation changes from the element-wise operation's one.
    //Tensor(x.map(f => density(f)))
    x.map(f => density(f))
  override def density2(x: Tensor): Double =
    density(x(0)) // Multivariate version is not available
  override def sample(): Tensor =
    var u = dist.sample()
    if u == 0.5 then
      u = dist.sample() // try again once
    val v = mean + sd * math.tan(math.Pi*u)
    Tensor(Array(v)) // Todo: Multivariate version is not available
  override def fill(n: Int): Seq[Double] =
    Seq.fill[Double](n)(sample()(0))

object CauchyDistribution:
  def fill(mean: Double, sd: Double, n: Int) =
    val dist = CauchyDistribution(mean, sd)
    Tensor(dist.fill(n))




