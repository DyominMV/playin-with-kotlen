package formallang.grammar

/**
 * Нужен для фильтрации возможных правил
 * Перед началом разбора подбирается к каждому нетерминалу, заодно 
 * выполняется поиск "плохих" символов и леворекурсивных правил (а мы их не любим)
 */
sealed class FirstCharacter{
  abstract public fun suitable(char: Char?): Boolean
  abstract operator fun plus(other: FirstCharacter): FirstCharacter
}

/**
 * Представляет собой два запрещённых множества (запрещено их объединение)
 */
class FirstCharBlackList(
  val charList: Set<Char?> = hashSetOf(), 
  val filter: CharFilter = {false}
): FirstCharacter(){
  override public fun suitable(char: Char?): Boolean = !(filter(char) || charList.contains(char))
  override operator fun plus(other: FirstCharacter): FirstCharacter 
    = when (other){
      is FirstCharBlackList -> FirstCharBlackList(
          (charList intersect other.charList) union 
          (charList.filter(other.filter)) union 
          (other.charList.filter(filter)), 
        {(filter(it) && other.filter(it))}
      )
      is FirstCharWhiteList -> this + FirstCharBlackList(other.charList, other.filter) 
    }
}

/**
 * Представляет собой два разрешённых множества (разрешено ТОЛЬКО их объединение)
 */
class FirstCharWhiteList(
  val charList: Set<Char?> = hashSetOf(), 
  val filter: CharFilter = {false}
): FirstCharacter(){
  override public fun suitable(char: Char?): Boolean = filter(char) || charList.contains(char)
  override operator fun plus(other: FirstCharacter): FirstCharacter 
    = when (other){
      is FirstCharBlackList -> other + FirstCharBlackList(charList, filter)
      is FirstCharWhiteList -> FirstCharWhiteList(charList union other.charList, {filter(it) || other.filter(it)})
    }
}

/**
 * терминал знает свой первый символ, так почему бы его не отдать?
 */
fun Terminal.getFirstChar(): FirstCharacter 
  = FirstCharWhiteList(charList = hashSetOf(this.value.get(0)))

/**
 * А вот и раскрываеся терминальная природа спецсимвола. 
 */
fun SpecialSymbol.getFirstChar(): FirstCharacter 
  = FirstCharWhiteList(filter = this.filter)