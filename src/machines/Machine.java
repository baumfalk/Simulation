package machines;

import java.util.ArrayList;

import misc.DVD;
import buffer.Buffer;

public abstract class Machine {
	public final int machineNumber;
	 // array list since stage 3 can access multiple buffers.
	protected final ArrayList<Buffer> leftBuffers;
	protected final ArrayList<Buffer> rightBuffers;
	
	protected final Buffer dvdsInMachine;
	
	public Machine(int machineNumber, ArrayList<Buffer> leftBuffers, ArrayList<Buffer> rightBuffers, int maxDVDInMachine)
	{
		this.machineNumber = machineNumber;
		this.leftBuffers = leftBuffers;
		this.rightBuffers = rightBuffers;
		dvdsInMachine = new Buffer(maxDVDInMachine);
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
	
	public Buffer leftBuffer(int i)
	{
		return this.leftBuffers.get(i);
	}
	
	public Buffer rightBuffer(int i)
	{
		return this.rightBuffers.get(i);
	}
	
	public Buffer leftBuffer()
	{
		return this.leftBuffer(0);
	}
	
	public Buffer rightBuffer()
	{
		return this.rightBuffer(0);
	}
	
	public abstract int generateProcessingTime();
}
