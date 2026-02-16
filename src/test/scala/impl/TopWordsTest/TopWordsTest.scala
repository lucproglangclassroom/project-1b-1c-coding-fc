package edu.luc.cs.cs371.topwords.core

import munit.FunSuite

class TopWordsTest extends FunSuite:

  test("TopWords orders by frequency descending then alphabetically") {
    val top = TopWords(3)
    top.onWordAdded("banana")
    top.onWordAdded("apple")
    top.onWordAdded("banana")
    top.onWordAdded("cherry")
    top.onWordAdded("apple")

    val result = top.topK()
    assertEquals(result, Vector(
      ("apple", 2),
      ("banana", 2),
      ("cherry", 1)
    ))
  }

  test("TopWords removes word when count drops to zero") {
    val top = TopWords(2)
    top.onWordAdded("hello")
    top.onWordRemoved("hello")
    assertEquals(top.topK(), Vector.empty)
  }

  test("TopWords respects howMany limit") {
    val top = TopWords(1)
    top.onWordAdded("a")
    top.onWordAdded("b")
    top.onWordAdded("b")
    assertEquals(top.topK(), Vector(("b", 2)))
  }
