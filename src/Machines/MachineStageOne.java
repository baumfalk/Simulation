package Machines;

import java.util.LinkedList;

import Misc.DVD;
import Stages.StateStageOne;

public class MachineStageOne extends Machine {

	public StateStageOne state; 
	
	private int lastBreakDownTime;
	private int processingTimeLeft;
	private int totalProcessingTime;

	private DVD dvdBeingProcessed;
	
	public MachineStageOne(int machineNumber,LinkedList<DVD>rightBuffer) {
		super(machineNumber,null,rightBuffer);
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

	public int getProcessingTimeLeft() {
		return processingTimeLeft;
	}

	public void setProcessingTimeLeft(int processingTimeLeft) {
		this.processingTimeLeft = processingTimeLeft;
	}

	public DVD getDvdBeingProcessed() {
		return dvdBeingProcessed;
	}

	public void setDvdBeingProcessed(DVD dvdBeingProcessed) {
		this.dvdBeingProcessed = dvdBeingProcessed;
	}

	public int getTotalProcessingTime() {
		return totalProcessingTime;
	}

	public void setTotalProcessingTime(int totalProcessingTime) {
		this.totalProcessingTime = totalProcessingTime;
	}
}
