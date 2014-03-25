package machines;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.LogNormalDistribution;

import states.StateStage1;
import buffer.DVDBuffer;
import exceptions.InvalidStateException;

public class MachineStage1 extends Machine {

	private StateStage1 state; 
	private LogNormalDistribution dist;
	private ExponentialDistribution distBreakdown;
	private ExponentialDistribution distRepair;
	
	public boolean eventScheduled;
	private int processingTime;
	private int timeOfBreakdown;
	
	public MachineStage1(int machineNumber,DVDBuffer rightBuffer) {
		super(machineNumber,null,new ArrayList<DVDBuffer>(Arrays.asList(rightBuffer)),1);
		
		state = StateStage1.Running;
		dist = new LogNormalDistribution(3.51, 1.23);
		distBreakdown = new ExponentialDistribution(8*60*60);
		distRepair = new ExponentialDistribution(2*60*60);
	}

	@Override
	public int generateProcessingTime() {
		// TODO Randomize
		return 1;//(int) Math.round(dist.sample());
	}
	
	public int generateBreakDownTime()
	{
		return (int) Math.round(distBreakdown.sample());
	}

	public int generateRepairTime()
	{
		return (int) Math.round(distRepair.sample());
	}


	public void setBroken() {
		if(state == StateStage1.Running || state == StateStage1.Blocked ||  state == StateStage1.BrokenAndRepaired)
			state = StateStage1.Broken;
		else {
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				e.printStackTrace();
				System.out.println("\t Cannot change the state of machine 1 to Broken with the state " + state);
				System.exit(1);
			}
		}
	}


	public StateStage1 getState() {

		return state;
	}

	public void setBrokenAndDVDBeforeRepair() {
		// TODO Auto-generated method stub
		if(state == StateStage1.Broken) {
			state = StateStage1.BrokenAndDVD;
			System.out.println("\t Set the state to BrokenAndDVDBeforeRepair" );
		}
		else {
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				e.printStackTrace();
				System.out.println("\t Cannot change the state of machine 1 to BrokenAndDVDBeforeRepair with the state " + state);
				System.exit(1);
			}
		}
		
	}

	public void setRunning() {
		if(state == StateStage1.Blocked || state == StateStage1.BrokenAndRepaired || state == StateStage1.BrokenAndDVD || state == StateStage1.Broken)
			state = StateStage1.Running;
		else {
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				e.printStackTrace();
				System.out.println("\t Cannot change the state of a machine of stage 1 to Running with the state " + state);
				System.exit(1);
			}
		}
		state = StateStage1.Running;
	}

	public void setBlocked() {
		if(state == StateStage1.Running)
			state = StateStage1.Blocked;
		else {
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				e.printStackTrace();
				System.out.println("\t Cannot change the state of a machine of stage 1 to Blocked with the state " + state);
				System.exit(1);
			}
		}
	}

	public void setBrokenAndRepaired() {
		// TODO Auto-generated method stub
		if(state == StateStage1.Broken)
			state = StateStage1.BrokenAndRepaired;
		else {
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				e.printStackTrace();
				System.out.println("\t Cannot change the state of a machine of stage 1 to BrokenAndRepairedBeforeDVD with the state " + state);
				System.exit(1);
			}
		}
	
	}

	public void setProcessingTime(int processingTime) {
		this.processingTime = processingTime;
	}

	public int getProcessingTime() {
		// TODO Auto-generated method stub
		return processingTime;
	}

	public void setTimeOfBreakdown(int timeOfBreakdown) {
		// TODO Auto-generated method stub
		this.timeOfBreakdown = timeOfBreakdown;
	}

	public int getTimeOfBreakdown() {
		return timeOfBreakdown;
	}
}
