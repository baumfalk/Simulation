package machines;

import java.util.ArrayList;
import java.util.Arrays;

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
