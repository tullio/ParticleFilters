package com.sofken.pf.test

import com.sofken.pf.MersenneTwister
import org.junit.Assert.assertThat
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.is
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Test

class MersenneTwisterTest {
  @Test
  def objectCreationTest {
    //val mt = new MersenneTwister(System.currentTimeMillis().toInt)
    val mt = new MersenneTwister()
    assertThat(mt, notNullValue())
    //assertThat(mt.nextInt(10), anyOf(is(0), is(1)))
    assertThat(mt.nextInt(10), equalTo(2))
    assertThat(mt.nextDouble, equalTo(0.9057919370756192))
    assertThat(mt.nextNorm(0.0, 1.0), equalTo(0.2543161358565558))
  }
}
