package machines;

import java.util.ArrayList;

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
	

	
	
	public abstract int generateProcessingTime();
}
