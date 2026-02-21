package edu.luc.cs.cs371.topwords.core

import munit.FunSuite

class WordStreamProcessorTest extends FunSuite:

  import WordStreamProcessor.*

  private def cfg(
    cloudSize: Int = 5,
    minLength: Int = 1,
    windowSize: Int = 10,
    everyKSteps: Int = 1,
    minFrequency: Int = 1,
    ignoreCase: Boolean = false
  ): Config =
    Config(cloudSize, minLength, windowSize, everyKSteps, minFrequency, ignoreCase)

  test("sliding window removes old words") {
    val out =
      WordStreamProcessor
        .clouds(Iterator("a", "b", "a"), cfg(windowSize = 2))
        .toVector

    // last emitted cloud should reflect only last 2 accepted words: "b", "a"
    val lastCloud = out.lastOption.getOrElse(Seq.empty)
    assertEquals(lastCloud, Seq(("a", 1), ("b", 1)))
  }

  test("ignores words shorter than minLength") {
    val out =
      WordStreamProcessor
        .clouds(Iterator("hi", "hello"), cfg(minLength = 3))
        .toVector

    val lastCloud = out.lastOption.getOrElse(Seq.empty)
    assertEquals(lastCloud, Seq(("hello", 1)))
  }

  test("ignoreCase merges words") {
    val out =
      WordStreamProcessor
        .clouds(Iterator("Apple", "apple"), cfg(ignoreCase = true))
        .toVector

    val lastCloud = out.lastOption.getOrElse(Seq.empty)
    assertEquals(lastCloud, Seq(("apple", 2)))
  }

  test("minFrequency filters output") {
    val out =
      WordStreamProcessor
        .clouds(Iterator("a", "b", "a"), cfg(minFrequency = 2))
        .toVector

    val lastCloud = out.lastOption.getOrElse(Seq.empty)
    assertEquals(lastCloud, Seq(("a", 2)))
  }

  test("emits only everyKSteps") {
    val out =
      WordStreamProcessor
        .clouds(Iterator("a", "b", "c"), cfg(everyKSteps = 2))
        .toVector

    // Only step 2 emits (since only accepted words increment steps)
    assertEquals(out.size, 1)
    assertEquals(out.head, Seq(("a", 1), ("b", 1)))
  }

  test("windowSize = 1") {
    val out =
      WordStreamProcessor
        .clouds(Iterator("x", "y"), cfg(windowSize = 1))
        .toVector

    val lastCloud = out.lastOption.getOrElse(Seq.empty)
    assertEquals(lastCloud, Seq(("y", 1)))
  }

  test("cloudSize truncates to top N words") {
    val out =
      WordStreamProcessor
        .clouds(Iterator("a", "b", "c"), cfg(cloudSize = 2))
        .toVector

    val lastCloud = out.lastOption.getOrElse(Seq.empty)
    assertEquals(lastCloud.size, 2)
  }

  test("no input does not crash") {
    val out =
      WordStreamProcessor
        .clouds(Iterator.empty, cfg())
        .toVector

    assertEquals(out.size, 0)
  }