package com.example.pf.model
import com.example.pf.distribution.NormalDistribution
import com.example.pf.Tensor
import org.nd4j.linalg.api.rng.DefaultRandom
class GaussianNoise(mean: Double, sd: Double, seed: Int = 0):
  val dist = NormalDistribution(seed, mean, sd)
  def probability(x: Tensor): Tensor =
    dist.density(x)

