package Machines;

import java.util.LinkedList;

import Misc.DVD;
import Stages.StateStageOne;

public class MachineStageOne extends Machine {

	public StateStageOne state; 
	
	public int lastBreakDownTime;
	public int lastRepairTime;
	public int processingTimeLeft;
	public int totalProcessingTime;

	public DVD dvdBeingProcessed;
	
	public MachineStageOne(int machineNumber,LinkedList<DVD>rightBuffer) {
		super(machineNumber,null,rightBuffer);
		// TODO Auto-generated constructor stub
		state = StateStageOne.Normal;
	}

	@Override
	public int generateProcessingTime() {
		// TODO Auto-generated method stub
		return machineNumber*10;
	}
	
	public float generateBreakDownTime()
	{
		return machineNumber*3;
	}

	public float generateRepairTime()
	{
		return machineNumber*2;
	}

	public int getLastBreakDownTime() {
		return lastBreakDownTime;
	}

	public void setLastBreakDownTime(int lastBreakDownTime) {
		this.lastBreakDownTime = lastBreakDownTime;
	}
}
