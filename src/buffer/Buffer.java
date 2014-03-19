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
	
	public void addBatchToBuffer(ArrayList<DVD> batch) {
		for(DVD dvd : batch) {
			addToBuffer(dvd);
		}
	}
	
	public void addToBuffer(DVD dvd)
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
	
	public DVD removeFromBuffer() {
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
		return dvdList.size()/maxSize;
	}
	
	public boolean isFull() {
		if(maxSize == -1) // infinite capacity 
			return false;
		return dvdList.size() >= maxSize;
	}

	public ArrayList<DVD> peekBuffer() {
		return new ArrayList<DVD>(dvdList);
	}

	public DVD peekDVD() {
		// TODO Auto-generated method stub
		return dvdList.peek();
	}
}
