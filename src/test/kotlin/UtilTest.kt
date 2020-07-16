import org.junit.Test
import org.junit.Assert.*
import util.*
import java.io.*
import kotlinx.coroutines.*

class UtilTest{
  @Test
  fun utilTest(){
    val reader = InputStreamReader("123456789!".byteInputStream())
    val stream = ForkableStream<Char>({
      val result = reader.read()
      if (-1 == result) null else result.toChar()
    })
    val jobs = ArrayList<Job>()
    runBlocking{
      stream.next()
      stream.next()
      stream.next()
    }
    for (i in 1 .. 5){
      jobs.add (GlobalScope.launch {
        val myStream = stream.fork(2).toList().get(0)
        println(myStream.next()!! + "\n")
      })
    }
    jobs.forEach { runBlocking{ it.join() } }
  }
}