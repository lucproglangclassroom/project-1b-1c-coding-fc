package edu.luc.cs.cs371.topwords.core

import munit.FunSuite

class WordStreamProcessorTest extends FunSuite:

  // A simple test observer to capture emitted clouds
  class TestObserver extends WordCloudObserver:
    var clouds: Vector[Seq[(String, Int)]] = Vector.empty
    override def onCloudUpdated(cloud: Seq[(String, Int)]): Unit =
      clouds = clouds :+ cloud

  test("sliding window removes old words") {
    val observer = TestObserver()
    val processor = WordStreamProcessor(5, 1, 2, 1, 1, false, observer)

    processor.processWord("a")  // step 1
    processor.processWord("b")  // step 2 -> cloud emitted
    processor.processWord("a")  // step 3 -> cloud emitted

    val lastCloud = observer.clouds.lastOption.getOrElse(Seq.empty)
    assertEquals(lastCloud, Seq(("a", 1), ("b", 1)))
  }

  test("ignores words shorter than minLength") {
    val observer = TestObserver()
    val processor = WordStreamProcessor(5, 3, 10, 1, 1, false, observer)

    processor.processWord("hi")     // too short, ignored
    processor.processWord("hello")  // long enough, triggers cloud

    val lastCloud = observer.clouds.lastOption.getOrElse(Seq.empty)
    assertEquals(lastCloud, Seq(("hello", 1)))
  }

  test("ignoreCase merges words") {
    val observer = TestObserver()
    val processor = WordStreamProcessor(5, 1, 10, 1, 1, true, observer)

    processor.processWord("Apple")
    processor.processWord("apple") // merged, triggers cloud

    val lastCloud = observer.clouds.lastOption.getOrElse(Seq.empty)
    assertEquals(lastCloud, Seq(("apple", 2)))
  }

  test("minFrequency filters output") {
    val observer = TestObserver()
    val processor = WordStreamProcessor(5, 1, 10, 1, 2, false, observer)

    processor.processWord("a")  // count = 1, cloud not emitted
    processor.processWord("b")  // count = 1, cloud not emitted
    processor.processWord("a")  // count = 2, triggers cloud

    val lastCloud = observer.clouds.lastOption.getOrElse(Seq.empty)
    assertEquals(lastCloud, Seq(("a", 2)))
  }

  test("emits only everyKSteps") {
    val observer = TestObserver()
    val processor = WordStreamProcessor(5, 1, 10, 2, 1, false, observer)

    processor.processWord("a")  // step 1, no cloud
    processor.processWord("b")  // step 2 -> cloud emitted
    processor.processWord("c")  // step 3, no cloud yet

    assertEquals(observer.clouds.size, 1)
  }

  test("windowSize = 1") {
    val observer = TestObserver()
    val processor = WordStreamProcessor(5, 1, 1, 1, 1, false, observer)

    processor.processWord("x")  // step 1 -> cloud
    processor.processWord("y")  // step 2 -> cloud, "x" removed due to window size

    val lastCloud = observer.clouds.lastOption.getOrElse(Seq.empty)
    assertEquals(lastCloud, Seq(("y", 1)))
  }

  test("cloudSize truncates to top N words") {
    val observer = TestObserver()
    val processor = WordStreamProcessor(2, 1, 10, 1, 1, false, observer)

    processor.processWord("a")
    processor.processWord("b")
    processor.processWord("c") // step 3, cloud emitted, only top 2 words kept

    val lastCloud = observer.clouds.lastOption.getOrElse(Seq.empty)
    assertEquals(lastCloud.size, 2)
  }

  test("no input does not crash") {
    val observer = TestObserver()
    val processor = WordStreamProcessor(5, 1, 10, 1, 1, false, observer)

    // no words processed

    assertEquals(observer.clouds.size, 0)
  }

