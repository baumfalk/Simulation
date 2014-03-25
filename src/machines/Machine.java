package machines;

import java.util.ArrayList;

import misc.DVD;
import buffer.DVDBuffer;

public abstract class Machine {
	public final int machineNumber;
	 // array list since stage 3 can access multiple buffers.
	protected final ArrayList<DVDBuffer> leftBuffers;
	protected final ArrayList<DVDBuffer> rightBuffers;
	
	protected final DVDBuffer dvdsInMachine;
	private int idleTime;
	private int blockedTime;
	
	public Machine(int machineNumber, ArrayList<DVDBuffer> leftBuffers, ArrayList<DVDBuffer> rightBuffers, int maxDVDInMachine)
	{
		this.machineNumber = machineNumber;
		this.leftBuffers = leftBuffers;
		this.rightBuffers = rightBuffers;
		dvdsInMachine = new DVDBuffer(maxDVDInMachine);
	}
	
	public boolean machineIsEmpty() {
		return dvdsInMachine.isEmpty();
	}
	
	public void addDVD(DVD dvd) {
		this.dvdsInMachine.addToBuffer(dvd);
	}
	
	public DVD removeDVD()
	{
		return this.dvdsInMachine.removeFromBuffer();
	}
	
	public DVD peekDVD()
	{
		return this.dvdsInMachine.peekDVD();
	}
	
	public DVDBuffer leftBuffer(int i)
	{
		return this.leftBuffers.get(i-1);
	}
	
	public DVDBuffer rightBuffer(int i)
	{
		return this.rightBuffers.get(i-1);
	}
	
	public DVDBuffer leftBuffer()
	{
		return this.leftBuffer(1);
	}
	
	public DVDBuffer rightBuffer()
	{
		return this.rightBuffer(1);
	}
	
	public abstract int generateProcessingTime();
	
	public void setTimeIdleStarted(int timeOfOccurrence) {
		idleTime = timeOfOccurrence;
	}
	
	public int getIdleTime() {
		return idleTime;
	}

	public void setTimeBlockedStarted(int timeOfOccurrence) {
		blockedTime = timeOfOccurrence;
	}
	
	public int getBlockedTime() {
		return blockedTime;
	}
}
