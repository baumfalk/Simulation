package machines;

import java.util.ArrayList;
import java.util.Arrays;

import misc.DVD;
import states.StateConveyorBelt;
import buffer.Buffer;
import exceptions.InvalidStateException;

public class ConveyorBelt extends Machine {

	private StateConveyorBelt state;
	public int beginDelayTime;
	public ConveyorBelt(int conveyorBeltNumber, Buffer rightBuffer) {
		super(conveyorBeltNumber, null, new ArrayList<Buffer>(Arrays.asList(rightBuffer)),-1); // infinity
		state = StateConveyorBelt.Idle;
	}
	
	@Override
	public int generateProcessingTime() {
		return 5*60;
	}

	public StateConveyorBelt getState() {
		return state;
	}

	public void setIdle() {
		if(state == StateConveyorBelt.Running)
			state = StateConveyorBelt.Idle;
		else {
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				e.printStackTrace();
				System.out.println("\t Cannot change the state of a conveyor belt to Idle with the state " + state);
				System.exit(1);
			}
		}
		
	}

	public void setBlocked() {
		if(state == StateConveyorBelt.Running)
			state = StateConveyorBelt.Blocked;
		else {
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				e.printStackTrace();
				System.out.println("\t Cannot change the state of a conveyor belt to Blocked with the state " + state);
				System.exit(1);
			}
		}
		
	}

	public void setRunning() {
		if(state == StateConveyorBelt.Idle || state == StateConveyorBelt.Blocked)
			state = StateConveyorBelt.Running;
		else {
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				e.printStackTrace();
				System.out.println("\t Cannot change the state of a conveyor belt to Running with the state " + state);
				System.exit(1);
			}
		}
	}

	public void updateExpectedFinishingTime(int i) {
		System.out.println("Updating finishing time");
		for(DVD dvd : this.dvdsInMachine.peekBuffer()) {
			dvd.expectedLeavingTimeConveyorBelt += i;
		}
	}

	public ArrayList<DVD> peekBuffer() {
		// TODO Auto-generated method stub
		return this.dvdsInMachine.peekBuffer();
	}

}
