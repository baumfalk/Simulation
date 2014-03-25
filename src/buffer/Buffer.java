package buffer;

import java.util.ArrayList;
import java.util.LinkedList;

import exceptions.BufferOverflowException;
import exceptions.BufferUnderflowException;

public class Buffer<T> {
	private LinkedList<T> dvdList;

	public final int maxSize;
	
	public Buffer(int maxSize)
	{
		this.maxSize = maxSize;
		dvdList = new LinkedList<T>();
	}
	
	public void addBatchToBuffer(ArrayList<T> batch) {
		for(T dvd : batch) {
			addToBuffer(dvd);
		}
	}
	
	public void addToBuffer(T dvd)
	{
		if(isFull()) {
			try {
				throw new BufferOverflowException();
			} catch (BufferOverflowException e) {
				e.printStackTrace();
				System.exit(1);
			}
		} else {
			dvdList.add(dvd);
		}
	}
	
	public T removeFromBuffer() {
		if(isEmpty()) {
			try {
				throw new BufferUnderflowException();
			} catch (BufferUnderflowException e) {
				e.printStackTrace();
				System.exit(1);
			}
		} else {
			return dvdList.pop();
		}
		return null;
		
	}
	
	public ArrayList<T> emptyBuffer()
	{
		ArrayList<T> list = new ArrayList<T>(dvdList);
		dvdList.clear();
		return list;
	}

	public boolean isEmpty() {
		return dvdList.size() == 0;
	}
	
	public int getNumberOfDVDs()
	{
		return dvdList.size();
	}

	public double currentLoad()
	{
		if(maxSize == -1)
			return 0;
		return dvdList.size()/maxSize;
	}
	
	public boolean isFull() {
		if(maxSize == -1) // infinite capacity 
			return false;
		return dvdList.size() >= maxSize;
	}

	public ArrayList<T> peekBuffer() {
		return new ArrayList<T>(dvdList);
	}

	public T peekDVD() {
		return dvdList.peek();
	}
}
