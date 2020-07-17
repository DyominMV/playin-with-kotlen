package forkable
 
interface IForkable<T>{
  /**
   * подразумевается, что после вызова этого метода, к объекту IForkable 
   * не будет обращений
   */
  public fun fork(count: Int): Iterable<IForkable<T>>
}