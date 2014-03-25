package machines;

import java.util.ArrayList;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

import exceptions.InvalidStateException;

import misc.DVD;
import states.StateStage2;
import states.StateStage3;
import buffer.DVDBuffer;

public class MachineStage3 extends Machine {

	private StateStage3 state;
	public final int batchSize;
	private ExponentialDistribution distStep1;
	private ExponentialDistribution distStep2;
	private UniformRealDistribution distFailure;
	public MachineStage3(int machineNumber, ArrayList<DVDBuffer> leftBuffers,
			ArrayList<DVDBuffer> rightBuffers, int maxDVDInMachine) {
		super(machineNumber, leftBuffers, rightBuffers, maxDVDInMachine);
		batchSize = maxDVDInMachine;
		state = StateStage3.Idle;
		distStep1 = new ExponentialDistribution(10);
		distStep2 = new ExponentialDistribution(6);
		distFailure = new UniformRealDistribution(0, 1);
	}

	@Override
	public int generateProcessingTime() {
		try {
			throw new Exception();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return -1; // do not use
	}
	
	public int generateProcessingTimeStep1() {
		double time = 0;
		for(int i = 0; i < batchSize; i++) {
			time += distStep1.sample();
		}
		return (int) Math.round(time);
	}
	
	public int generateProcessingTimeStep2() {
		double time = 0;
		for(int i = 0; i < batchSize; i++) {
			time += distStep2.sample();
		}
		return (int) Math.round(time);
	}
	
	public boolean machineStuckOnDVD() {
		return distFailure.sample() <= 0.03;
	}
	
	public int generateRepairTime() {
		return 5*60;//TODO: randomize
	}
	
	public int generateProcessingTimeStep3() {
		return 3 * 60;
	}
	
	public void addBatch(ArrayList<DVD> batch) {
		
		this.dvdsInMachine.addBatchToBuffer(batch);
	}
	
	public ArrayList<DVD> peekBatch()
	{
		return this.dvdsInMachine.peekBuffer();
	}
	
	public ArrayList<DVD> removeBatch() {
		return this.dvdsInMachine.emptyBuffer();
	}

	public StateStage3 getState() {
		return state;
	}

	public void setRunning() {
		if(state == StateStage3.Idle || state == StateStage3.Blocked)
			state = StateStage3.Running;
		else {
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				e.printStackTrace();
				System.out.println("\t Cannot change the state of a machine of stage 3 to Running with the state " + state);
				System.exit(1);
			}
		}
	}

	public void setIdle() {
		if(state == StateStage3.Running)
			state = StateStage3.Idle;
		else {
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				e.printStackTrace();
				System.out.println("\t Cannot change the state of a machine of stage 3 to Idle with the state " + state);
				System.exit(1);
			}
		}
	}

	public void setBlocked() {
		if(state == StateStage3.Running)
			state = StateStage3.Blocked;
		else {
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				e.printStackTrace();
				System.out.println("\t Cannot change the state of a machine of stage 3 to Blocked with the state " + state);
				System.exit(1);
			}
		}
	}
}
