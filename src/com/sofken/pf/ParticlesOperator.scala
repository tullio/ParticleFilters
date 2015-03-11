package com.sofken.pf

trait ParticlesOperator[A] {
//  def +(a:Particle[A], b:Particle[A]):Particle[A]
//  def unit:Particle[A]
//  def +(a:A, b:A):A
  def +(a:Particle[A], b:Particle[A]):Particle[A]
  def unit:A
}
//object ParticlesPlus {
//  implicit object IntPlus extends ParticlesOperator[Int] {
//    def +(a:Particle[Int], b:Particle[Int])(implicit s:ParticlesOperator[Int]): Particle[Int] =
//      s.+(a,b)
//    def unit: Particle[Int] = new Particle[Int]()
//  }
//  implicit object DoublePlus extends ParticlesOperator[Double] {
//    def +(a:Double, b:Double): Double = a + b
//    def unit: Particle[Double] = new Particle[Double]()
//  }
//}