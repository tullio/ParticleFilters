package com.sofken.pf

trait ParticleOperator[A] {
  def +(a:A, b:A):A
  def unit:A
}
object ParticlePlus {
  implicit object IntPlus extends ParticleOperator[Int] {
    def +(a:Int, b:Int): Int = a + b
    def unit: Int = 0
  }
  implicit object DoublePlus extends ParticleOperator[Double] {
    def +(a:Double, b:Double): Double = a + b
    def unit: Double = 0.0
  }
}
