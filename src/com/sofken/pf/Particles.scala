package com.sofken.pf

case class Particles[A]() {
  var p = Vector[Particle[A]]()
  var number:Int = _
  var initValue:A = _
  println(p)
  def resize(number:Int, dimension:Int, init:A):Int = {
	  initValue = init
	  if(number > this.number) {
		  p = p.padTo(number - this.number , new Particle(dimension, init))
	  } else {
		  p = p.take(number)
	  }
	  this.number = number
	  return this.number
  }
  def resize(number:Int, dimension:Int):Int = {
    return resize(number, dimension, initValue)
  }
}