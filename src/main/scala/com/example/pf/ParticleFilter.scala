package com.example.pf
import com.example.pf.model._
import com.example.pf.Tensor
/**
 * ParticleFilter
 * @param systemModel
 * @param observationModel
 */
class ParticleFilter(systemModel: SystemModel,
                     observationModel: ObservationModel):
  val resampling = Resampling()
  val xSeriese = scala.collection.mutable.ListBuffer.empty[Tensor]
  val ySeriese = scala.collection.mutable.ListBuffer.empty[Tensor]
  var m: Int = _
  var weight: Tensor = _
  var debug: Boolean = false
  /**
   * step
   * Produce particles for the next time from current particles with/without
   * observed particles.
   * @param x Current latent variable particles(Double * number of particles)
   * @param y Current observation(Double)
   * @param predictOnly If true, only a system model is performed. Otherwise,
   *                    the result performed by a system model is revised by
   *                    the resampling method with an observation model.
   * @return Updated latent variable particles(Double * number of particles)
   */
  def step(x: Tensor, y: Tensor, predictOnly: Boolean = false): Tensor =
    m = x.length.toInt
    val f = systemModel.systemModel(x)
    if debug then
      println(s"x(${x.length})=${x}")
      println(s"f(${f.length})=${f} (ParticleFilter)")
    var newParticle: Tensor = new Tensor()
    if predictOnly == false then
      weight = observationModel.observationNoiseProbabirity(y, f)
      if debug then
        println(s"y(${y.length})=${y} (ParticleFilter)")
        println(s"a(${weight.length})=${weight} (ParticleFilter)")
      newParticle = resampling.systematicResampling(f, weight)
    else
      newParticle = f
    newParticle
  def logLikelihood: Double =
    math.log(weight.sum)/m



