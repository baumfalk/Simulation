package machines;

import java.util.ArrayList;
import java.util.Arrays;

import misc.DVD;
import states.StateStage1;
import buffer.Buffer;

public class MachineStage1 extends Machine {

	public StateStage1 state; 
	
	public int lastBreakDownTime;
	public int lastRepairTime;
	public int processingTimeLeft;
	public int totalProcessingTime;

	public MachineStage1(int machineNumber,Buffer rightBuffer) {
		super(machineNumber,null,new ArrayList<Buffer>(Arrays.asList(rightBuffer)),1);
		
		state = StateStage1.Running;
	}

	public void addDVD(DVD dvd) {
		this.dvdsInMachine.addToBuffer(dvd);
		System.out.println("\t Added dvd to stage 1 machine " + machineNumber);
	}
	
	public DVD removeDVD()
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
