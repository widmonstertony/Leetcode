import java.util.Iterator;
import java.util.NoSuchElementException;

/*
 * @lc app=leetcode id=284 lang=java
 *
 * [284] Peeking Iterator
 */

// @lc code=start
// Java Iterator interface reference:
// https://docs.oracle.com/javase/8/docs/api/java/util/Iterator.html

class PeekingIterator implements Iterator<Integer> {
	private Iterator<Integer> iter;
	private boolean hasPeeked;
	private int nextNum;
	public PeekingIterator(Iterator<Integer> iterator) {
	    // initialize any member here.
		hasPeeked = false;
		iter = iterator;
	}
	
    // Returns the next element in the iteration without advancing the iterator.
	public Integer peek() {
        if (hasPeeked) {
			return nextNum;
		}
		if (!hasNext()) {
			// throw new NoSuchElementException();
		}
		nextNum = next();
		hasPeeked = true;
		return nextNum;
	}
	
	// hasNext() and next() should behave the same as in the Iterator interface.
	// Override them if needed.
	@Override
	public Integer next() {
	    if (hasPeeked) {
			hasPeeked = false;
			return nextNum;
		}
		else {
			if (!hasNext()) {
				// throw new NoSuchElementException();
			}
			return iter.next();
		}
	}
	
	@Override
	public boolean hasNext() {
	    if (hasPeeked) {
			return true;
		}
		else {
			return iter.hasNext();
		}
	}
}
// @lc code=end

