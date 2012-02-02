package testtools

object Stopwatch {
  def time[T](task: => T): (T, Long) = {
    val start = System.currentTimeMillis()
    val result = task
    val end = System.currentTimeMillis()
    (result, end - start)
  }
}