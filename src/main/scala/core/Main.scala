package edu.luc.cs.cs371.topwords.app

import edu.luc.cs.cs371.topwords.core.*
import scala.io.Source
import mainargs.*
import org.slf4j.LoggerFactory

object Main:

  private val logger = LoggerFactory.getLogger("topwords")

  @main
  def run(
    @arg(name = "cloud-size", short = 'c')
    cloudSize: Int = 10,

    @arg(name = "length-at-least", short = 'l')
    minLength: Int = 6,

    @arg(name = "window-size", short = 'w')
    windowSize: Int = 1000,

    @arg(name = "every-k-steps", short = 'k')
    everyKSteps: Int = 1,

    @arg(name = "min-frequency", short = 'f')
    minFrequency: Int = 1,

    @arg(name = "ignore-case")
    ignoreCase: Boolean = false
  ): Unit =

    logger.debug(
      s"howMany=$cloudSize minLength=$minLength windowSize=$windowSize everyKSteps=$everyKSteps minFrequency=$minFrequency ignoreCase=$ignoreCase"
    )

    val observer = ConsoleObserver()

    val processor =
      WordStreamProcessor(
        cloudSize,
        minLength,
        windowSize,
        everyKSteps,
        minFrequency,
        ignoreCase,
        observer
      )

    val lines = Source.stdin.getLines()

    import scala.language.unsafeNulls

    val words =
      lines.flatMap(line =>
        line.split("(?U)[^\\p{Alpha}0-9']+")
      )

    for word <- words do
      processor.processWord(word)
