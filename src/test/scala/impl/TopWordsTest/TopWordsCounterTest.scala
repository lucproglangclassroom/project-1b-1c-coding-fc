package edu.luc.cs.cs371.topwords.core

import munit.FunSuite

class TopWordsCounterTest extends FunSuite:

  test("formatCloud formats correctly") {
    val input = Seq(("apple", 2), ("banana", 1))
    val result = TopWordsCounter.formatCloud(input)
    assertEquals(result, "apple: 2 banana: 1")
  }

  test("formatCloud handles empty list") {
    assertEquals(TopWordsCounter.formatCloud(Seq.empty), "")
  }
