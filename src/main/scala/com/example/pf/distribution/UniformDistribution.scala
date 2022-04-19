package com.example.pf.distribution

import org.nd4j.linalg.api.rng.distribution.impl.UniformDistribution as JavaUniformDistribution
class UniformDistribution(lower: Double, upper: Double, dim: Int = 1) extends BaseDistribution:
  override val dist = new JavaUniformDistribution(lower, upper)



