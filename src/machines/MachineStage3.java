package machines;

import java.util.ArrayList;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

import misc.DVD;
import states.StateStage3;
import buffer.Buffer;

public class MachineStage3 extends Machine {

	public StateStage3 state;
	public final int batchSize;
	private ExponentialDistribution distStep1;
	private ExponentialDistribution distStep2;
	private UniformRealDistribution distFailure;
	public MachineStage3(int machineNumber, ArrayList<Buffer> leftBuffers,
			ArrayList<Buffer> rightBuffers, int maxDVDInMachine) {
		super(machineNumber, leftBuffers, rightBuffers, maxDVDInMachine);
		batchSize = maxDVDInMachine;
		state = StateStage3.Idle;
		distStep1 = new ExponentialDistribution(10);
		distStep2 = new ExponentialDistribution(6);
		distFailure = new UniformRealDistribution(0, 1);
	}

	@Override
	public int generateProcessingTime() {
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
		return 5*60;
	}
	
	public int generateProcessingTimeStep3() {
		return 3 * 60;
	}
	
	public void addBatch(ArrayList<DVD> batch) {
		
		System.out.println(this.dvdsInMachine.currentDVDCount());
		this.dvdsInMachine.addBatchToBuffer(batch);
		System.out.println("\t added batch to stage 3 machine " + machineNumber);
	}
	
	public ArrayList<DVD> peekBatch()
	{
		return this.dvdsInMachine.peekBuffer();
	}
	
	
	
	public ArrayList<DVD> removeBatch() {
		return this.dvdsInMachine.emptyBuffer();
	}

}
