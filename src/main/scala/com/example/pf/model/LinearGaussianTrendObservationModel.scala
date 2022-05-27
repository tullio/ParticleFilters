package com.example.pf.model
import com.example.pf.Tensor
import com.example.pf._
import com.example.pf.distribution.NormalDistribution
/**
 *
 * @param mean
 * @param sd
 */
class LinearGaussianTrendObservationModel(mean: Double, sd: Double) extends ObservationModel:
  val observationNoise = new NormalDistribution(mean, sd)

  /**
   *
   * @param h Particles for the observed variables of the dimension d.
   * @param x Particles for the latent variable of the dimension 2*d.
   * @return com.example.pf.Tensor of the dimension d.
   */
  def inversedObservationModel(h: Tensor, x: Tensor): Tensor =
    val m = x.length/2
    //h - x
    (h - x(0, m)) ++ Tensor.repeat(0.0, m.toInt)
  def observationNoiseProbability(h: Tensor, x: Tensor): Tensor =
    val v = inversedObservationModel(h, x)
    observationNoise.density(v)