package machines;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.distribution.LogNormalDistribution;

import states.StateStage1;
import buffer.Buffer;
import exceptions.InvalidStateException;

public class MachineStage1 extends Machine {

	private StateStage1 state; 
	private LogNormalDistribution dist;
	public int lastBreakDownTime;
	public int lastRepairTime;
	public int processingTimeLeft;
	public int totalProcessingTime;

	public MachineStage1(int machineNumber,Buffer rightBuffer) {
		super(machineNumber,null,new ArrayList<Buffer>(Arrays.asList(rightBuffer)),1);
		
		state = StateStage1.Running;
		dist = new LogNormalDistribution(3.51, 1.23);
	}

	@Override
	public int generateProcessingTime() {
		// TODO Randomize
		return (int) Math.round(dist.sample());
	}
	
	public int generateBreakDownTime()
	{
		return 8*60*60;
	}

	public int generateRepairTime()
	{
		return 2*60*60;
	}


	public void setBroken() {
		if(state == StateStage1.Running)
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

	public void setBrokenAndBlocked() {
		if(state == StateStage1.Blocked)
			state = StateStage1.BrokenAndBlocked;
		else {
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				e.printStackTrace();
				System.out.println("\t Cannot change the state of a machine of stage 1 to BrokenAndBlocked with the state " + state);
				System.exit(1);
			}
		}
	}

	public StateStage1 getState() {

		return state;
	}

	public void setBrokenAndDVDBeforeRepair() {
		// TODO Auto-generated method stub
		if(state == StateStage1.Broken)
			state = StateStage1.BrokenAndDVDBeforeRepair;
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
		if(state == StateStage1.Blocked || state == StateStage1.BrokenAndRepairedBeforeDVD || state == StateStage1.BrokenAndDVDBeforeRepair)
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
		if(state == StateStage1.Running || state == StateStage1.BrokenAndBlocked)
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

	public void brokenAndRepairedBeforeDVD() {
		// TODO Auto-generated method stub
		if(state == StateStage1.Broken)
			state = StateStage1.BrokenAndRepairedBeforeDVD;
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
}
