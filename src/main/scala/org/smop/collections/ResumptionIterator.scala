package org.smop.collections

/** Iterates over a succession of iterators.
  * @param delegate the first inner iterator
  * @param resume function transforming the current state to a new iterator and a new state.
  * The end is signalled by returning an empty iterator from resume.
  * @param state the starting state */
class ResumptionIterator[T,S](var delegate: Iterator[T], resume: (S) => Product2[Iterator[T], S], var state: S) extends Iterator[T] {
  def next: T = delegate.next

  def hasNext = delegate.hasNext || {
      val (newIterator, newState) = resume(state)
      delegate = newIterator
      state = newState
      delegate.hasNext
    }
}
