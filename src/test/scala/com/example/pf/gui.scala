package com.example.pf

import org.scalatest.{BeforeAndAfterEachTestData, Suite, TestData}

trait gui extends BeforeAndAfterEachTestData { this: Suite =>
  var enableX11: Boolean = _
  override def beforeEach(testData: TestData): Unit =
    val cm = testData.configMap
    enableX11 =
      cm.getWithDefault[String]("X11", "false") match
        case "true" => true
        case "false" => false


    super.beforeEach(testData)
}

