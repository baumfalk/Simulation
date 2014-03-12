package machines;

import java.util.ArrayList;
import java.util.Arrays;

import exceptions.BufferOverflowException;
import exceptions.BufferUnderflowException;
import misc.DVD;
import states.StateStageTwo;
import buffer.Buffer;

public class MachineStageTwo extends Machine {

	public StateStageTwo state;
	public MachineStageTwo(int machineNumber, Buffer leftBuffer) {
		super(machineNumber,new ArrayList<Buffer>(Arrays.asList(leftBuffer)),null,1);
		state = StateStageTwo.Idle;
	}

	public void addDVD(DVD dvd) throws BufferOverflowException {
		this.dvdsInMachine.addToBuffer(dvd);
		System.out.println("\t Added dvd to stage 2 machine " + machineNumber);
	}
	
	public DVD removeDVD() throws BufferUnderflowException
	{
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
