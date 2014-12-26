package com.sofken.pf

case class Filter() {

  val x, y, v = Particles[Double]()
  val alpha= Vector[Double]()
  var w:Double = _
  var number:Int = _
  var dimension:Int = _
  val mt = new MersenneTwister(System.currentTimeMillis().toInt)
  var stateFunc:(Particles[Double], Particles[Double])=>Particles[Double] = _
  var robserveFunc:(Particles[Double], Particles[Double]) => Double = _
  var robserveJacobianFunc:(Particles[Double], Particles[Double]) => Double = _
  var robserveDensityFunc:Double => Double = _
  /**
   * Reset random sequence. 
   */
  def setSeed(seed:Int) = {
    mt.setSeed(seed)
  }
  def createInitialParticles():Boolean = {
    if(number == 0 || dimension == 0) {
      false
    } else {
      x.resize(number, dimension)
      v.resize(number, dimension)
      true
    }
  }
}