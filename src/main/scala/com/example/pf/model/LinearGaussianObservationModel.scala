package com.example.pf.model
import com.example.pf.Tensor
import com.example.pf._
import com.example.pf.distribution.NormalDistribution
/**
 * LinearGaussianObservationModel
 * y = x + noise, noise = NormalDistribution(mean, sd)
 * @param mean
 * @param sd
 */
class LinearGaussianObservationModel(mean: Double, sd: Double) extends ObservationModel:
  val observationNoise = new NormalDistribution(mean, sd)
  println(s"NormalDistribution(${mean}, ${sd})")
  def inversedObservationModel(h: Tensor, x: Tensor): Tensor =
      h - x
  def observationNoiseProbability(h: Tensor, x: Tensor): Tensor =
      val v = inversedObservationModel(h, x)
      //println(s"diff between hidden variable and observation=${v}")
      observationNoise.density(v)
  override def toString() =
      s"LinearGaussianObservationModel(${mean}, ${sd})"
