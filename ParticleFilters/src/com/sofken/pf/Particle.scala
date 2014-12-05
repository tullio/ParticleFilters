package com.sofken.pf


class Particle[A] {
  var p = Seq[A]()
  var dimension:Int = -1
  
  def this(dimension:Int) = {
    this()
    this.dimension = dimension
    
  }

}