package com.example.pf.distribution
import com.example.pf.Tensor

import org.nd4j.linalg.api.rng.DefaultRandom
import org.nd4j.linalg.api.rng.distribution.impl.NormalDistribution as JavaNormalDistribution
class NormalDistribution(seed: Int, mean: Double, sd: Double) extends BaseDistribution:
  val rnd = new DefaultRandom(seed.toLong)
  override val dist = new JavaNormalDistribution(rnd, mean, sd)
  def this(mean: Double, sd: Double) =
    this(0, mean, sd)
object NormalDistribution:
  def fill(mean: Double, sd: Double, n: Int) =
    val dist = NormalDistribution(mean, sd)
    Tensor(dist.fill(n))




