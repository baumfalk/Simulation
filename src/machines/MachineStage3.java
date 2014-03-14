package machines;

import java.util.ArrayList;

import exceptions.BufferOverflowException;
import misc.DVD;
import states.StateStage3;
import buffer.Buffer;

public class MachineStage3 extends Machine {

	public StateStage3 state;
	private int batchSize;
	public MachineStage3(int machineNumber, ArrayList<Buffer> leftBuffers,
			ArrayList<Buffer> rightBuffers, int maxDVDInMachine) {
		super(machineNumber, leftBuffers, rightBuffers, maxDVDInMachine);
		batchSize = maxDVDInMachine;
		state = StateStage3.Idle;
	}

	@Override
	public int generateProcessingTime() {
		return -1; // do not use
	}
	
	public int generateProcessingTimeStep1() {
		return batchSize * 10;
	}
	
	public int generateProcessingTimeStep2() {
		return batchSize * 6;
	}
	
	public int generateProcessingTimeStep3() {
		return 3 * 60;
	}
	
	public void addBatch(ArrayList<DVD> batch) {
		
		try {
			this.dvdsInMachine.addBatchToBuffer(batch);
		} catch (BufferOverflowException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("\t added batch to stage 3 machine " + machineNumber);
	}
	
	public Buffer leftBuffer(int i)
	{
		return this.leftBuffers.get(i);
	}
	
	public Buffer rightBuffer(int i)
	{
		return this.rightBuffers.get(i);
	}
	
	public ArrayList<DVD> removeBatch() {
		return this.dvdsInMachine.emptyBuffer();
	}

}
