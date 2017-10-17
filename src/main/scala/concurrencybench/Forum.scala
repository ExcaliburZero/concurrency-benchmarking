package concurrencybench

trait Forum {
  def readThread(threadId: Int): String
  def replyToThread(threadId: Int, msg: String): Unit
  def createThread(): Unit
  def getRandomThreadId(): Int
}
