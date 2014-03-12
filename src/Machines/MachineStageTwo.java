package Machines;

import java.util.LinkedList;

import Misc.DVD;
import Stages.StateStageTwo;

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
