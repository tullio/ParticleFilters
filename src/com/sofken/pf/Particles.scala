package com.sofken.pf

case class Particles[A]() {
  var p = scala.collection.mutable.ArrayBuffer[Particle[A]]()
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
     def apply(k:Int):Particle[A] = {
     p(k)
   }
   def update(k:Int, x:Particle[A]) = {
     p(k)=x
   }
   def iterator() = {
     p.iterator
   }
      def set(x:Particles[A]) = {
     p = x.p   
   }
   def set(x:scala.collection.mutable.ArrayBuffer[Particle[A]]) = {
     p = x
   }
}