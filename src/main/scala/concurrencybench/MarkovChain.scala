package concurrencybench

import collection.mutable.ArrayBuffer
import collection.mutable.HashMap

import java.util.concurrent.ThreadLocalRandom

object MarkovChain {
  val START = "</Start\\>"
  val END = "</End\\>"
}

class MarkovChain(wordLimit: Int) {
  val words = new HashMap[String,ArrayBuffer[String]]()

  def addSentence(sentence: String): Unit = {
    val sentenceWords = MarkovChain.START +: sentence.split(" ") :+ MarkovChain.END

    if (sentenceWords.length > 0) {
      for (i <- (0 until sentenceWords.length).init) yield addWord(sentenceWords, i)
    } else {
      throw new IllegalArgumentException()
    }
  }

  private def addWord(sentenceWords: Array[String], current: Int): Unit = {
    val word = sentenceWords(current)
    val nextWord = sentenceWords(current + 1)

    val suffixes = words.get(word) match {
      case Some(prev) =>
        prev
      case None =>
        val newSuffixes = new ArrayBuffer[String]()
        words.put(word, newSuffixes)
        newSuffixes
    }

    suffixes + nextWord
  }

  def generateSentence(): String = {
    val random = ThreadLocalRandom.current()

    val sentence = new StringBuilder()
    var currentWord = MarkovChain.START

    var count = 0
    while (count < wordLimit && currentWord != MarkovChain.END) {
      val nextPossibilities = words.get(currentWord).get

      val i = random.nextInt(0, nextPossibilities.size)
      currentWord = nextPossibilities(i)
      sentence.append(currentWord + " ")

      count += 1
    }

    sentence.toString().init
  }
}
