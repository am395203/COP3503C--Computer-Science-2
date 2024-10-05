//COP3503C-20Summer C001 -Skip List
//Torres Amanda 
//References 
//Skip Lists: A Probabilistic Alternative to Balanced Trees - 
// https://15721.courses.cs.cmu.edu/spring2018/papers/08-oltpindexes1/pugh-skiplists-cacm1990.pdf 
//Skip Lists: Done Right - http://ticki.github.io/blog/skip-lists-done-right/
//Implementing the skip list data structure - http://www.mathcs.emory.edu/~cheung/Courses/323/Syllabus/Map/skip-list-impl.html


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;


public class SkipListSet<T extends Comparable<T>> implements SortedSet<T>,Set<T>,Collection<T>,Iterable<T>  {

	private class SkipListSetNode<E extends Comparable<E>>{
		E data;
		SkipListSetNode<E>[] next; //keep track of levels nodes are on, next[0] -> pointers on level 0, next[1] -> pointers on level 1...etc
		
		@SuppressWarnings("unchecked")
		public SkipListSetNode(E val, int level) {
			this.data = val; //set data to value of val
			next = new SkipListSetNode[level + 1]; //create new array of pointers depending on generated level
			
			for(int i = 0; i < level; i++) { 
				next[i] = null;
			}
		}
	}
	private int maxLvl;
	private int size;
	private SkipListSetNode<T> head;
	
	
	public SkipListSet() { //create new skip list with no elements 
		head = new SkipListSetNode<T>(null,1);
		maxLvl = 1;
		size = 0;
	}
	
	public SkipListSet(Collection<? extends T> c){ //create new skip list with elements from a collection
		head = new SkipListSetNode<T>(null,1);
		addAll(c);
		
	}
	
	private int getRandLvl() { //generate a random level
		int randLvl = 0;
		Random ranNum = new Random();
		
		while(ranNum.nextInt(50) % 2 == 0 && randLvl < maxLvl){ // keep increasing randLvl if we get %2 == 0 and its less than max level
			randLvl++;
		}
		return randLvl;
	}
	
	private int maxLevelGenerate() { //max level based on # of elements
		int max = (int)Math.floor(Math.log(size)/Math.log(2));
		return max;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean contains(Object val) {
		SkipListSetNode<T> temp = head;
		
		for(int i = maxLvl; i >= 0; i--) { //search from top layer through bottom
			while(temp.next[i] != null && temp.next[i].data.compareTo((T) val)<0) {
				temp = temp.next[i]; //continue till we reach null (not there) or get to element before
			}
		}
		temp = temp.next[0]; //next element should be the one we are searching for
		if(temp != null && temp.data.compareTo((T) val) == 0) {//if it is return true, else return false
			return true;
		}
		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean add(T val) {
		if(contains(val)) { //if the value is in the list then return false
			return false;
		}
		size++;
		int getMax = maxLevelGenerate(); //generate the new level
		
		if(getMax > maxLvl) { //if the new max is greater than the old one, must increase the head 
			increaseHead(getMax);
		}
		int newLvl = getRandLvl();
		SkipListSetNode<T>[] up = new SkipListSetNode[maxLvl + 1]; //keep track of where our next val should go
		SkipListSetNode<T> temp = head;

		for(int i = maxLvl; i>=0; i--) {
			while(temp.next[i] != null && temp.next[i].data.compareTo(val) < 0) {
				temp = temp.next[i];
			}
			up[i] = temp; //keep track of each expected placement on the level
		}
		temp = new SkipListSetNode(val,newLvl); //create the new node

		for(int i = 0; i<= newLvl; i++) { //then for 0 - generated level set to up, and up to temp
			temp.next[i] = up[i].next[i];
			up[i].next[i] = temp;
		}
		return true;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void increaseHead(int newLvl) { //increases the level of the head when max level is surpassed
		SkipListSetNode<T> temp = head;
		head = new SkipListSetNode(null,newLvl);
		
		for(int i = 0; i <= maxLvl; i++) {
			head.next[i] = temp.next[i];
		}
		maxLvl = newLvl;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object val) {
		if(!contains(val)) { //if the item is not there, return false 
			return false;
		}
		SkipListSetNode<T> temp = head;
		for(int i = maxLvl; i>=0; i--) {
			while(temp.next[i] != null && temp.next[i].data.compareTo((T) val) < 0) {
				temp = temp.next[i];
			}
			if(temp.next[i] != null && temp.next[i].data.compareTo((T) val) == 0) { //if we found the val
				temp.next[i] = temp.next[i].next[i]; //set temp.[next] to temp.[next].[next]
			}	
		}
		size--; //reduce the size
		return true;
	}

	public void reBalance() { 
		SkipListSetNode<T> temp = head;//set temp to head so our values arent lost
		int getMax = maxLevelGenerate();//generate the max level from the size we have
		head = new SkipListSetNode<T>(null,getMax);
		maxLvl = getMax;
		
		while(temp.next[0] != null) { //add all the values into the newly created skip list 
			addReb(temp.next[0].data);
			temp = temp.next[0];
		}
	}
	
	private boolean addReb(T val) { //add for rebalance(), same as add but dont have to worry about increasing the head
		if(contains(val)) {
			return false;
		}
		int newLvl = getRandLvl();

		@SuppressWarnings("unchecked")
		SkipListSetNode<T>[] up = new SkipListSetNode[maxLvl + 1];
		SkipListSetNode<T> temp = head;
		
		for(int i = maxLvl; i>=0; i--) {
			while(temp.next[i] != null && temp.next[i].data.compareTo(val) < 0) {
				temp = temp.next[i];
			}
			up[i] = temp;
		}
		temp = new SkipListSetNode<T>(val,newLvl);
		
		for(int i = 0; i<= newLvl; i++) {
			temp.next[i] = up[i].next[i];
			up[i].next[i] = temp;
		}
		return true;
	}
	
	
	@Override
	public Iterator<T> iterator() { //return new skip list iterator
		return new SkipListSetIterator<T>();
	}
	
	@SuppressWarnings("hiding")
	private class SkipListSetIterator<T extends Comparable<T>> implements Iterator<T>{
		private SkipListSet<T>.SkipListSetNode<T> curr = null;
		
		public boolean hasNext(){
			if(curr == null && head != null) { //if head != null then there is an item
				return true;
			}
			else if(curr != null) { //if curr != null, return
				return curr.next[0] != null;
			}
			return false; //else return false since there is nothing left
		}

		@SuppressWarnings("unchecked")
		public T next(){
			if(curr == null && head != null) {
				curr = (SkipListSet<T>.SkipListSetNode<T>) head.next[0];
				return (T) head.next[0].data;
			}
			else if(curr != null) {
				curr = curr.next[0];
				return curr.data;
			}
			throw new NoSuchElementException();
		}	
	}
	
	@Override
	public Object[] toArray() {
		Object[] objArr = new Object[size];
		SkipListSetNode<T> temp = head;
		for(int i = 0; i < size; i++) {
			objArr[i] = temp.next[0].data;
			temp = temp.next[0];
		}
		return objArr;
	}
	
	@SuppressWarnings({ "hiding", "unchecked", "rawtypes" })
	@Override
	public <T> T[] toArray(T[] a) {
		if (a.length < size) { 
			  a = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
			} else if (a.length > size) {
			  a[size] = null;
			}
		int i = 0;
		SkipListSetNode temp = head;
		while(temp.next[0] != null) {
			a[i] = (T) temp.next[0].data;
			temp = temp.next[0];
			i++;
		}
		return a;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean status = false;
		ArrayList<T> list = new ArrayList<T>();
		SkipListSetNode<T> temp = head;
		
		while(temp.next[0] != null) {
			if(!c.contains(temp.next[0].data)) { //if the item isnt in c
				list.add((T) temp.next[0].data); //add it to our list to be deleted
				status = true;
			}
			temp = temp.next[0];
		}
		removeAll(list); //remove all of the items in our list 
		
		return status;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		if(((Set<T>) o).size() == size) {
			if(containsAll((Collection<?>) o)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		SkipListSetNode<T> temp = head;
		int sum = 0;
		
		while(temp.next[0] != null) {
			sum += temp.next[0].data.hashCode();
			temp = temp.next[0];
		}
		return sum;	
	}
	
	@Override
	public boolean addAll(Collection<? extends T> c) {
		c.forEach( element -> { add(element); });
		return true;
	}
	
	@Override
	public boolean containsAll(Collection<?> c) { 
		@SuppressWarnings("unchecked")
		Iterator<T> itr = (Iterator<T>) c.iterator();
		while(itr.hasNext()) {
			if(!contains(itr.next())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		c.forEach( element -> { remove(element); });
		return true;
	}

	@Override
	public SortedSet<T> subSet(T fromElement, T toElement) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SortedSet<T> headSet(T toElement) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SortedSet<T> tailSet(T fromElement)  {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void clear() {
		head = new SkipListSetNode<T>(null,0);
		maxLvl = 0;
		size = 0;
	}
	
	@Override
	public T first() {
		SkipListSetNode<T> temp = head;
		return temp.next[0].data;
	}
	
	@Override
	public T last() {
		SkipListSetNode<T> temp = head;
		while(temp.next[0] != null) {
			temp = temp.next[0];
		}
		return temp.data;
	}
	
	@Override
	public int size() {
		return size;
	}
	
	@Override
	public boolean isEmpty() {
		if(size == 0) {
			return true;
		}
		return false;
	}

	@Override
	public Comparator<? super T> comparator() {
		return null;
	}
	
}
