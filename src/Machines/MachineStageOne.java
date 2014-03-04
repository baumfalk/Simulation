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
		state = StateStageOne.Running;
	}

	@Override
	public int generateProcessingTime() {
		// TODO Auto-generated method stub
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

	public int getLastBreakDownTime() {
		return lastBreakDownTime;
	}

	public void setLastBreakDownTime(int lastBreakDownTime) {
		this.lastBreakDownTime = lastBreakDownTime;
	}
}
