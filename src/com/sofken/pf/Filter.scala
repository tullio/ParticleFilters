package com.sofken.pf

case class Filter() {

  val x, y, v = Particles[Double]()
  var alpha= Vector[Double]()
  var w:Double = _
  var number:Int = _
  var dimension:Int = _
  val mt = new MersenneTwister(System.currentTimeMillis().toInt)
  var stateFunc:(Particles[Double], Particles[Double])=>Particles[Double] = _
  var robserveFunc:(Particles[Double], Particle[Double]) => Double = _
  var robserveJacobianFunc:(Particles[Double], Particle[Double]) => Double = _
  var robserveDensityFunc:Double => Double = _
  /**
   * Reset random sequence. 
   */
  def setSeed(seed:Int) = {
    mt.setSeed(seed)
  }
  def createInitialParticles(init:Double):Boolean = {
    println("number="+number)
    println("dimension="+dimension)
    if(number == 0 || dimension == 0) {
      return false
    } else {
      x.resize(number, dimension, init)
      v.resize(number, dimension, init)
      alpha = alpha.padTo(number, 0.0)
      println("alpha="+alpha.length)
      return true
    }
  }
  def createSystemNoise(mean:Double, dist:Double):Boolean = {
    //Range(0, v.p.length - 1).foreach(i => println(v.p(i)))
    //v.p.foreach(i => Range(0, i.dimension ).foreach(j => {i.p(j) = 0.0}))
    v.iterator.foreach(p => for(i <- 0 to p.dimension - 1) {
      p(i) = mt.nextNorm(mean, dist)
      println("e=" + p(i))
    })
    true
  }
  def getSystemNoise() = {
    v
  }
  def setStateFunc(func:(Particles[Double], Particles[Double]) => Particles[Double]) = {
    stateFunc = func
  }
  def getNextState = {
    x.set(stateFunc(x, v))
  }
  def setRobserveFunc(func:(Particles[Double], Particle[Double]) => Double) = {
    robserveFunc = func
  }
  def getObservedNoise(i:Int) = {
    robserveFunc(y, x(i))
  }
  def setRobserveJacobianFunc(func:(Particles[Double], Particle[Double]) => Double) = {
    robserveJacobianFunc = func
  }
  def setRobserveDensityFunc(func:(Double) => Double) = {
    robserveDensityFunc = func
  }
  def getRobservedDensityValue(w:Double) = {
    robserveDensityFunc(w)
  }
  def setObservedData(p: Particles[Double]) = {
    y.set(p)
    println("set:"+y.dump + " num="+y.number )
  }
  def computLikelihood() = {
    for (i <- 0 to alpha.length - 1) {
      alpha = alpha.updated(i, robserveDensityFunc(robserveFunc(y, x(i)))
                        * robserveJacobianFunc(y, x(i)))
    }
  }
  def getParticles() = {
    x
  }
  def resampling() = {
    var alphaSum = 0.0
    alphaSum = alpha.iterator.reduce((x, y)=> x + y)
    var f = new Particles[Double](number, dimension)
    var pSuma = Vector[Double]()
    pSuma = pSuma :+ 0.0
    for(i <- 1 to x.number) {
      var s = 0.0
      for(j <- 0 to i - 1) {
        s = s + alpha(j)
      }
      pSuma = pSuma :+ s
    }
    println("pSuma="+pSuma)
    for(j <- 0 to x.number - 1) {
      val u = ((j+1)-0.5)/x.number 
      var sampleIndex = -1
      def findIndex {
    	  for(i <- 1 to x.number ) {
    		  val a1 = pSuma(i-1)
    		  val a2 = pSuma(i)
    		  if((a1/alphaSum < u) && (a2/alphaSum >= u)) {
    			  sampleIndex = i - 1
    			  return
    		  }
    	  }
      }
      findIndex
      if(sampleIndex >= 0) {
        f(j) = x(sampleIndex)      
      }
    }
    x.set(f)
  }
}
