package concurrencybench

trait Forum {
  type ForumThread = Either[String,String]

  def readThread(threadId: Int): Option[String]
  def replyToThread(threadId: Int, msg: String): Boolean
  def createThread(): Unit
  def getRandomThreadId(): Int
  def closeThread(threadId: Int): Unit
}
