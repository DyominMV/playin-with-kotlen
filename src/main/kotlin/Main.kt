fun main(args: Array<String>) {
  if ((null as Boolean?) is Boolean?) println("Okay") else println("not okay")
  if ((null as Any) is Boolean) println("Okay") else println("not okay")
}
