package machines;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.distribution.UniformRealDistribution;

import states.StateStage4;
import buffer.DVDBuffer;
import exceptions.InvalidStateException;

public class MachineStage4 extends Machine {

	public StateStage4 state;
	private UniformRealDistribution dist;
	private int dvdsLeft;
	public MachineStage4(int machineNumber, DVDBuffer leftBuffer) {
		super(machineNumber,new ArrayList<DVDBuffer>(Arrays.asList(leftBuffer)),null,1);
		state = StateStage4.Idle;
		dist = new UniformRealDistribution(20, 30);
		renewCartridge();
	}
	
	public boolean cartridgeIsEmpty()
	{
		return dvdsLeft == 0;
	}
	
	public void decreaseToner()
	{
		dvdsLeft--;
	}
	
	public int renewCartridgeIfNecessary() {
		int delay = 0;
		if(cartridgeIsEmpty()) {
			delay = generateCartridgeRenewalTime();
			renewCartridge();
		}
		return delay;
	}
	
	private void renewCartridge() {
		dvdsLeft = 200; // todo fix this
	}
	
	private int generateCartridgeRenewalTime() {
		return 15*60; // todo fix this
	}

	@Override
	public int generateProcessingTime() {
		return 1+(int) Math.round(dist.sample());
	}

	public StateStage4 getState() {
		return state;
	}

	public void setRunning() {
		if(state == StateStage4.Idle)
			state = StateStage4.Running;
		else {
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				e.printStackTrace();
				System.out.println("\t Cannot change the state of a machine of stage 4 to Running with the state " + state);
				System.exit(1);
			}
		}
	}

	public void setIdle() {
		if(state == StateStage4.Running)
			state = StateStage4.Idle;
		else {
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				e.printStackTrace();
				System.out.println("\t Cannot change the state of a machine of stage 4 to Idle with the state " + state);
				System.exit(1);
			}
		}
	}
}
