fun toJSON(collection: Collection<Int>): String {
  val builder = StringBuilder("[")
  val iterator: Iterator<Int> =collection.iterator()
  while (iterator.hasNext()){
    builder.append(iterator.next())
    if (iterator.hasNext()) builder.append(", ")
  }
  builder.append("]")
  return builder.toString()
}

fun joinOptions(options: Collection<String>) = options.joinToString(",", "[", "]")


fun main(args: Array<String>) {
  println(joinOptions(arrayListOf("opt1","opt2")))
}