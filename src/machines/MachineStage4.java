package machines;

import java.util.ArrayList;
import java.util.Arrays;

import states.StateStage4;
import buffer.Buffer;

public class MachineStage4 extends Machine {

	public StateStage4 state;
	private int dvdsLeft;
	public MachineStage4(int machineNumber, Buffer leftBuffer) {
		super(machineNumber,new ArrayList<Buffer>(Arrays.asList(leftBuffer)),null,1);
		state = StateStage4.Idle;
		
	}
	
	public int dvdLeft()
	{
		return dvdsLeft;
	}
	
	public void decreaseDVDsLeft()
	{
		dvdsLeft--;
	}
	
	
	public void generateCartridgeRenewal() {
		dvdsLeft = 200;
	}

	@Override
	public int generateProcessingTime() {
		return 25;
	}

}
