package machines;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.distribution.UniformRealDistribution;

import states.StateStage4;
import buffer.DVDBuffer;

public class MachineStage4 extends Machine {

	public StateStage4 state;
	private UniformRealDistribution dist;
	private int dvdsLeft;
	public MachineStage4(int machineNumber, DVDBuffer leftBuffer) {
		super(machineNumber,new ArrayList<DVDBuffer>(Arrays.asList(leftBuffer)),null,1);
		state = StateStage4.Idle;
		dist = new UniformRealDistribution(20, 30);
	}
	
	public int dvdLeft()
	{
		return dvdsLeft;
	}
	
	public void decreaseDVDsLeft()
	{
		dvdsLeft--;
	}
	
	
	public void generateCartridgeRenewal() {
		dvdsLeft = 200; // todo fix this
	}
	
	public int generateCartridgeRenewalTime() {
		return 15*60; // todo fix this
	}

	@Override
	public int generateProcessingTime() {
		return (int) Math.round(dist.sample());
	}

}
