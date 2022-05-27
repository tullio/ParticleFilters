package com.example.pf.model
import com.example.pf.Tensor
import com.example.pf.distribution.BaseDistribution
trait ObservationModel:
  val observationNoise: BaseDistribution
  def inversedObservationModel(h: Tensor, x: Tensor): Tensor
  def observationNoiseProbability(y: Tensor, f: Tensor): Tensor