package edu.luc.cs.cs371.topwords.core

import munit.FunSuite

class TopWordsTest extends FunSuite:

  test("TopWords orders by frequency descending then alphabetically") {
    val top =
      TopWords(3)
        .onWordAdded("banana")
        .onWordAdded("apple")
        .onWordAdded("banana")
        .onWordAdded("cherry")
        .onWordAdded("apple")

    val result = top.topK()
    assertEquals(
      result,
      Vector(
        ("apple", 2),
        ("banana", 2),
        ("cherry", 1)
      )
    )
  }

  test("TopWords removes word when count drops to zero") {
    val top =
      TopWords(2)
        .onWordAdded("hello")
        .onWordRemoved("hello")

    assertEquals(top.topK(), Vector.empty)
  }

  test("TopWords respects howMany limit") {
    val top =
      TopWords(1)
        .onWordAdded("a")
        .onWordAdded("b")
        .onWordAdded("b")

    assertEquals(top.topK(), Vector(("b", 2)))
  }