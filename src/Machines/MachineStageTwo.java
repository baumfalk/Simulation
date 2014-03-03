package Machines;

import java.util.LinkedList;

import Misc.DVD;

public class MachineStageTwo extends Machine {

	public MachineStageTwo(int machineNumber, LinkedList<DVD>leftBuffer, LinkedList<DVD>rightBuffer) {
		super(machineNumber,leftBuffer,rightBuffer);
	}

	@Override
	public float generateProcessingTime() {
		return machineNumber;
	}
	
	public boolean breakDVD()
	{
		return true;
	}
}
