import grammar.*
import kotlinx.coroutines.*

class Main {
  companion object {
    @JvmStatic public fun main(args: Array<String>) {
      println("Start")
      // Start a coroutine
      GlobalScope.launch {
        delay(1000)
        println("Hello")
      }
      Thread.sleep(2000) // wait for 2 seconds
      println("Stop")
    }
  }
}
