package machines;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.distribution.UniformRealDistribution;

import states.StateStage4;
import buffer.Buffer;

public class MachineStage4 extends Machine {

	public StateStage4 state;
	private UniformRealDistribution dist;
	private int dvdsLeft;
	public MachineStage4(int machineNumber, Buffer leftBuffer) {
		super(machineNumber,new ArrayList<Buffer>(Arrays.asList(leftBuffer)),null,1);
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
		dvdsLeft = 200;
	}
	
	public int generateCartridgeRenewalTime() {
		return 15*60;
	}

	@Override
	public int generateProcessingTime() {
		return (int) Math.round(dist.sample());
	}

}
