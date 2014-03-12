package machines;

import java.util.LinkedList;

import misc.DVD;

public abstract class Machine {
	public final int machineNumber;
	public final LinkedList<DVD> leftBuffer;
	public final LinkedList<DVD> rightBuffer;
	
	public Machine(int machineNumber, LinkedList<DVD> leftBuffer, LinkedList<DVD> rightBuffer)
	{
		this.machineNumber = machineNumber;
		this.leftBuffer = leftBuffer;
		this.rightBuffer = rightBuffer;
	}
	
	
	
	public abstract int generateProcessingTime();
}