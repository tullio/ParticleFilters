package com.example.pf.model
import com.example.pf.Tensor
import com.example.pf.distribution.BaseDistribution

trait SystemModel:
  val systemNoise: BaseDistribution
  def systemModel(x: Tensor): Tensor
