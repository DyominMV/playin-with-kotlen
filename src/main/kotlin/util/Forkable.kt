package util
 
interface IForkable<T>{
  public suspend fun fork(count: Int): Iterable<IForkable<T>>
}