enum class DumbState{
  STATE1, STATE2, STATE3, STATE4, STATE5{
    override public fun isFinite(): Boolean = true  
  }, 
  STATE6{
    override public fun isFinite(): Boolean = true
  };
  public open fun isFinite(): Boolean = false
  
  public fun transition(): List<DumbState> = when (this){
    DumbState.STATE1 -> arrayListOf(DumbState.STATE2, DumbState.STATE3)
    DumbState.STATE2 -> arrayListOf(DumbState.STATE3, DumbState.STATE4)
    DumbState.STATE3 -> arrayListOf(DumbState.STATE4, DumbState.STATE5)
    DumbState.STATE4 -> arrayListOf(DumbState.STATE5, DumbState.STATE6)
    DumbState.STATE5 -> arrayListOf(DumbState.STATE5)
    DumbState.STATE6 -> arrayListOf(DumbState.STATE6)
  }
}