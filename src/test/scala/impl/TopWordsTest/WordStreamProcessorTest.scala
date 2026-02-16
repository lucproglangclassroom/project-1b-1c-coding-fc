package edu.luc.cs.cs371.topwords.core

import munit.FunSuite
import scala.collection.mutable

class WordStreamProcessorTest extends FunSuite:

  class TestObserver extends WordCloudObserver:
    var clouds: Vector[Seq[(String, Int)]] = Vector.empty
    override def onCloudUpdated(cloud: Seq[(String, Int)]): Unit =
      clouds = clouds :+ cloud

  test("sliding window removes old words") {
    val observer = TestObserver()
    val processor = WordStreamProcessor(5, 1, 2, 1, 1, false, observer)

    processor.processWord("a")
    processor.processWord("b")
    processor.processWord("a")

    val lastCloud = observer.clouds.last
    assertEquals(lastCloud, Seq(("a", 1), ("b", 1)))
  }

  test("ignores words shorter than minLength") {
    val observer = TestObserver()
    val processor = WordStreamProcessor(5, 3, 10, 1, 1, false, observer)

    processor.processWord("hi")
    processor.processWord("hello")

    val lastCloud = observer.clouds.last
    assertEquals(lastCloud, Seq(("hello", 1)))
  }

  test("ignoreCase merges words") {
    val observer = TestObserver()
    val processor = WordStreamProcessor(5, 1, 10, 1, 1, true, observer)

    processor.processWord("Apple")
    processor.processWord("apple")

    val lastCloud = observer.clouds.last
    assertEquals(lastCloud, Seq(("apple", 2)))
  }

  test("minFrequency filters output") {
    val observer = TestObserver()
    val processor = WordStreamProcessor(5, 1, 10, 1, 2, false, observer)

    processor.processWord("a")
    processor.processWord("b")
    processor.processWord("a")

    val lastCloud = observer.clouds.last
    assertEquals(lastCloud, Seq(("a", 2)))
  }

  test("emits only everyKSteps") {
    val observer = TestObserver()
    val processor = WordStreamProcessor(5, 1, 10, 2, 1, false, observer)

    processor.processWord("a")
    processor.processWord("b")
    processor.processWord("c")

    assertEquals(observer.clouds.size, 1)
  }

  test("windowSize = 1") {
    val observer = TestObserver()
    val processor = WordStreamProcessor(5, 1, 1, 1, 1, false, observer)

    processor.processWord("x")
    processor.processWord("y")
    val lastCloud = observer.clouds.last
    assertEquals(lastCloud, Seq(("y", 1)))
  }

  test("no input does not crash") {
    val observer = TestObserver()
    val processor = WordStreamProcessor(5, 1, 10, 1, 1, false, observer)
    assertEquals(observer.clouds.size, 0)
  }
