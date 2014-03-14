package machines;

import java.util.ArrayList;
import java.util.Arrays;

import states.StateConveyorBelt;
import buffer.Buffer;

public class ConveyorBelt extends Machine{

	public StateConveyorBelt state;
	
	public ConveyorBelt(int conveyorBeltNumber, Buffer rightBuffer) {
		super(conveyorBeltNumber, null, new ArrayList<Buffer>(Arrays.asList(rightBuffer)),-1); // infinity
		state = StateConveyorBelt.Idle;
	}
	
	@Override
	public int generateProcessingTime() {
		return 5*60;
	}

}
