package machines;

import java.util.ArrayList;
import java.util.Arrays;

import misc.DVD;
import states.StateConveyorBelt;
import buffer.Buffer;
import exceptions.BufferOverflowException;
import exceptions.BufferUnderflowException;

public class ConveyorBelt extends Machine{

	public StateConveyorBelt state;
	
	public ConveyorBelt(int conveyorBeltNumber, Buffer rightBuffer) {
		super(conveyorBeltNumber, null, new ArrayList<Buffer>(Arrays.asList(rightBuffer)),-1); // infinity
		state = StateConveyorBelt.Idle;
	}
	
	public void addDVD(DVD dvd) throws BufferOverflowException {
		this.dvdsInMachine.addToBuffer(dvd);
	}
	
		
	public DVD removeDVD() throws BufferUnderflowException
	{
		return this.dvdsInMachine.removeFromBuffer();
	}
	
	public Buffer rightBuffer()
	{
		return this.rightBuffers.get(0);
	}
	

	public int generateProcessingTime() {
		return 5*60;
	}

}
