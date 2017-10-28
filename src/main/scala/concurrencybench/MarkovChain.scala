package concurrencybench

import collection.mutable.ArrayBuffer
import collection.mutable.HashMap

import java.util.concurrent.ThreadLocalRandom

object MarkovChain {
  val START = "</Start\\>"
  val END = "</End\\>"

  val ENDING_LENGTH = (" " + END + " ").length
}

class MarkovChain(private val wordLimit: Int) {
  val words = new HashMap[String,ArrayBuffer[String]]()
  val startWords = new ArrayBuffer[String]()

  def addFromFile(filePath: String): Unit = {
    val source = scala.io.Source.fromFile(filePath)
    val lines = try source.getLines mkString "\n" finally source.close()

    addAllSentences(lines)
  }

  def addAllSentences(sentences: String): Unit = {
    val allSentences = sentences
      .replace("\t", "")
      .replace("!", ".")
      .replace("?", ".")
      .replace("\n", " ")
      .split("\\.").map(_.trim()).filter(_.length > 0)

    for (s <- allSentences) yield addSentence(s) 
  }

  def addSentence(sentence: String): Unit = {
    val sentenceWords = MarkovChain.START +: sentence.split(" ").filter(_.length > 0) :+ MarkovChain.END

    if (sentenceWords.length > 0) {
      for (i <- (0 until sentenceWords.length).init) yield addWord(sentenceWords, i)
    } else {
      throw new IllegalArgumentException()
    }
  }

  private def addWord(sentenceWords: Array[String], current: Int): Unit = {
    val word = sentenceWords(current)
    val nextWord = sentenceWords(current + 1)
    if (word == MarkovChain.START) {
      startWords += nextWord
      return
    }

    val prevWord = sentenceWords(current - 1)

    val key = prevWord + " " + word
    val suffixes = words.get(key) match {
      case Some(prev) =>
        prev
      case None =>
        val newSuffixes = new ArrayBuffer[String]()
        words.put(key, newSuffixes)
        newSuffixes
    }

    suffixes += nextWord
  }

  def generateSentence(): String = {
    if (startWords.size == 0) {
      throw new Exception()
    }

    val random = ThreadLocalRandom.current()

    val sentence = new StringBuilder()
    var prevWord = MarkovChain.START

    val startI = random.nextInt(0, startWords.size)
    var currentWord = startWords(startI)

    var count = 0
    while (count < wordLimit && currentWord != MarkovChain.END) {
      val key = prevWord + " " + currentWord
      val nextPossibilities = words.get(key).get

      val i = random.nextInt(0, nextPossibilities.size)
      prevWord = currentWord
      currentWord = nextPossibilities(i)
      sentence.append(currentWord + " ")

      count += 1
    }

    sentence.toString().dropRight(MarkovChain.ENDING_LENGTH)
  }
}
