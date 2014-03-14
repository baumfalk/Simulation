package machines;

import java.util.ArrayList;
import java.util.Arrays;

import misc.DVD;
import states.StateStage2;
import buffer.Buffer;

public class MachineStage2 extends Machine {

	public StateStage2 state;
	public MachineStage2(int machineNumber, Buffer leftBuffer) {
		super(machineNumber,new ArrayList<Buffer>(Arrays.asList(leftBuffer)),null,1);
		state = StateStage2.Idle;
	}

	public void addDVD(DVD dvd) {
		this.dvdsInMachine.addToBuffer(dvd);
		System.out.println("\t Added dvd to stage 2 machine " + machineNumber);
	}
	
	public DVD removeDVD() {
		System.out.println("\t removed dvd from stage 2 machine " + machineNumber);
		return this.dvdsInMachine.removeFromBuffer();
	}

	
	public Buffer leftBuffer()
	{
		return this.leftBuffers.get(0);
	}
	
	@Override
	public int generateProcessingTime() {
		//TODO: randomize
		return 24;
	}

	public boolean breakDVD()
	{
		return false;
	}
}
