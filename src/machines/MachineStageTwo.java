package machines;

import java.util.LinkedList;

import misc.DVD;
import states.StateStageTwo;

public class MachineStageTwo extends Machine {

	public StateStageTwo state;
	public DVD dvd;
	public MachineStageTwo(int machineNumber, LinkedList<DVD>leftBuffer, LinkedList<DVD>rightBuffer) {
		super(machineNumber,leftBuffer,rightBuffer);
		state = StateStageTwo.Idle;
	}

	@Override
	public int generateProcessingTime() {
		return 24;
	}

	public boolean breakDVD()
	{
		return false;
	}
}
