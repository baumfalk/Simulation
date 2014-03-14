package buffer;

import java.util.ArrayList;
import java.util.LinkedList;

import misc.DVD;
import exceptions.BufferOverflowException;
import exceptions.BufferUnderflowException;

public class Buffer {
	private LinkedList<DVD> dvdList;

	public final int maxSize;
	
	public Buffer(int maxSize)
	{
		this.maxSize = maxSize;
		dvdList = new LinkedList<DVD>();
	}
	
	public void addBatchToBuffer(ArrayList<DVD> batch) throws BufferOverflowException {
		for(DVD dvd : batch) {
			addToBuffer(dvd);
		}
	}
	
	public void addToBuffer(DVD dvd) throws BufferOverflowException
	{
		if(isFull()) {
			throw new BufferOverflowException();
		} else {
			dvdList.add(dvd);
		}
	}
	
	public DVD removeFromBuffer() throws BufferUnderflowException{
		if(isEmpty()) {
			throw new BufferUnderflowException();
		} else {
			return dvdList.pop();
		}
		
	}
	
	public ArrayList<DVD> emptyBuffer()
	{
		ArrayList<DVD> list = new ArrayList<DVD>(dvdList);
		dvdList.clear();
		return list;
	}

	public boolean isEmpty() {
		return dvdList.size() == 0;
	}
	
	public int currentDVDCount()
	{
		return dvdList.size();
	}

	public double currentLoad()
	{
		if(maxSize == -1)
			return 0;
		return (double)(dvdList.size()/maxSize);
	}
	
	public boolean isFull() {
		if(maxSize == -1) // infinite capacity 
			return false;
		return dvdList.size() >= maxSize;
	}
}
