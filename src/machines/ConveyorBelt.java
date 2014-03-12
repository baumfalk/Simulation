package machines;

import java.util.LinkedList;

import misc.DVD;
import states.StateConveyorBelt;

public class ConveyorBelt {

	public LinkedList<DVD> dvdsOnBelt;
	public LinkedList<Integer> dvdsOnBeltTime;
	public int timePaused;
	public final int conveyorBeltNumber;
	public StateConveyorBelt state;
	
	public ConveyorBelt(int conveyorBeltNumber) {
		this.conveyorBeltNumber = conveyorBeltNumber;
		dvdsOnBelt = new LinkedList<DVD>();
		dvdsOnBeltTime = new LinkedList<Integer>();
		state = StateConveyorBelt.Running;
	}

	public int generateProcessingTime() {
		return 5*60;
	}

}
