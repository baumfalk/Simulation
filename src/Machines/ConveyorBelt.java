package Machines;

import java.util.LinkedList;

import Misc.DVD;
import Stages.StateConveyorBelt;

public class ConveyorBelt {

	public LinkedList<DVD> dvdsOnBelt;
	public LinkedList<Integer> dvdsOnBeltTime;
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
