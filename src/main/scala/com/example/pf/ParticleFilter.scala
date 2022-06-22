package com.example.pf
import com.example.pf.model._
import com.example.pf.Tensor
import org.tinylog.Logger


/**
 * ParticleFilter
 * @param systemModel
 * @param observationModel
 */
class ParticleFilter(var systemModel: SystemModel,
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
    //Logger.debug("input x.length(# of particles)={}, y={}", x.length, y)
    m = x.length.toInt
    val f = systemModel.systemModel(x)
    //if debug then
      //prinln(s"x(${x.length})=${x}")
      //println(s"f(${f.length})=${f} (ParticleFilter)")
    var newParticle: Tensor = new Tensor()
    if predictOnly == false then
      weight = observationModel.observationNoiseProbability(y, f)
      if weight.sum < 1e-5 then
          Logger.error("weights are too small: {}", weight.sum)
          Logger.info("maybe system noise noise is too small? {}", systemModel)
          Logger.info("maybe observation noise is too small? {}", observationModel)
          Logger.debug("diff between hidden variable and observation={}", 
                observationModel.inversedObservationModel(y, f))
          Logger.debug("observation={}, system prediction={}", y, f)
          Logger.error("observation({})={} (ParticleFilter)", y.length, y)
          Logger.error("logLikelihood=log({}/{}={})", weight.sum, m, math.log(weight.sum/m))
        //println(s"weight(${weight.length})=${weight} (ParticleFilter)")
        //println(s"local logLikelihood=${logLikelihood}")
          newParticle = null
      else
          newParticle = resampling.systematicResampling(f, weight)
    else
      newParticle = f
    newParticle
  def logLikelihood: Double =
    //if debug then
    //    Logger.debug("log({}/{}={})", weight.sum, m, math.log(weight.sum/m))
    math.log(weight.sum/m)

  def getSystemModel =
    systemModel

  def getObservationModel =
    observationModel
  def setSystemModel(model: SystemModel) =
    systemModel = model
    this



