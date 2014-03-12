package machines;

import java.util.ArrayList;
import java.util.Arrays;

import misc.DVD;
import states.StateStageOne;
import buffer.Buffer;
import exceptions.BufferOverflowException;
import exceptions.BufferUnderflowException;

public class MachineStageOne extends Machine {

	public StateStageOne state; 
	
	public int lastBreakDownTime;
	public int lastRepairTime;
	public int processingTimeLeft;
	public int totalProcessingTime;

	public MachineStageOne(int machineNumber,Buffer rightBuffer) {
		super(machineNumber,null,new ArrayList<Buffer>(Arrays.asList(rightBuffer)),1);
		
		state = StateStageOne.Running;
	}

	public void addDVD(DVD dvd) throws BufferOverflowException {
		this.dvdsInMachine.addToBuffer(dvd);
		System.out.println("\t Added dvd to stage 1 machine " + machineNumber);
	}
	
	public DVD removeDVD() throws BufferUnderflowException
	{
		System.out.println("\t removed dvd from stage 1 machine " + machineNumber);
		return this.dvdsInMachine.removeFromBuffer();
	}
	
	public Buffer rightBuffer()
	{
		return this.rightBuffers.get(0);
	}

	@Override
	public int generateProcessingTime() {
		// TODO Randomize
		return 60;
	}
	
	public int generateBreakDownTime()
	{
		return 8*60*60;
	}

	public int generateRepairTime()
	{
		return 2*60*60;
	}

	
}
