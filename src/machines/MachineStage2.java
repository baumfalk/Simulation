package machines;

import java.util.ArrayList;
import java.util.Arrays;

import states.StateStage2;
import buffer.Buffer;

public class MachineStage2 extends Machine {

	public StateStage2 state;
	public MachineStage2(int machineNumber, Buffer leftBuffer) {
		super(machineNumber,new ArrayList<Buffer>(Arrays.asList(leftBuffer)),null,1);
		state = StateStage2.Idle;
	}

	@Override
	public int generateProcessingTime() {
		//TODO: randomize
		return 24;
	}

	public boolean breakDVD()
	{
		return false;
	}
}
