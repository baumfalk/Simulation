package machines;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.distribution.NormalDistribution;

import states.StateStage2;
import buffer.Buffer;
import exceptions.InvalidStateException;

public class MachineStage2 extends Machine {

	private StateStage2 state;
	private NormalDistribution dist;
	
	public MachineStage2(int machineNumber, Buffer leftBuffer) {
		super(machineNumber,new ArrayList<Buffer>(Arrays.asList(leftBuffer)),null,1);
		state = StateStage2.Idle;
		
		double theta = 0.0414757;
		// see wikipedia for how to convert
		// theta = sqrt(pi)/(sigma*sqrt(2))
		double sigma = (1/(theta/(Math.sqrt(Math.PI))))/(Math.sqrt(2));
		dist = new NormalDistribution(0,sigma);

	}

	@Override
	public int generateProcessingTime() {
		return (int) Math.abs(Math.round(dist.sample())); // wrap around
	}

	public boolean breakDVD()
	{
		return false;
	}

	public void setRunning() {
		// TODO Auto-generated method stub
		if(state == StateStage2.Idle || state == StateStage2.Blocked)
			state = StateStage2.Running;
		else {
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				e.printStackTrace();
				System.out.println("\t Cannot change the state of a machine of stage 2 to Running with the state " + state);
				System.exit(1);
			}
		}
	}

	public StateStage2 getState() {
		// TODO Auto-generated method stub
		return state;
	}

	public void setIdle() {
		// TODO Auto-generated method stub
		if(state == StateStage2.Running)
			state = StateStage2.Idle;
		else {
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				e.printStackTrace();
				System.out.println("\t Cannot change the state of a machine of stage 2 to Idle with the state " + state);
				System.exit(1);
			}
		}
	}

	public void setBlocked() {
		// TODO Auto-generated method stub
		if(state == StateStage2.Running)
			state = StateStage2.Blocked;
		else {
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				e.printStackTrace();
				System.out.println("\t Cannot change the state of a machine of stage 2 to Blocked with the state " + state);
				System.exit(1);
			}
		}
	}
}
