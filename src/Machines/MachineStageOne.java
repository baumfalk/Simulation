package Machines;

import Stages.StateStageOne;

public class MachineStageOne extends Machine {

	public StateStageOne state; 
	
	private int lastBreakDownTime;
	public MachineStageOne(int machineNumber) {
		super(machineNumber);
		// TODO Auto-generated constructor stub
		state = StateStageOne.Normal;
	}

	@Override
	public float generateProcessingTime() {
		// TODO Auto-generated method stub
		return machineNumber;
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
