package machines;

import java.util.ArrayList;
import java.util.Arrays;

import exceptions.InvalidStateException;

import states.StateStage2;
import buffer.Buffer;

public class MachineStage2 extends Machine {

	private StateStage2 state;
	public MachineStage2(int machineNumber, Buffer leftBuffer) {
		super(machineNumber,new ArrayList<Buffer>(Arrays.asList(leftBuffer)),null,1);
		state = StateStage2.Idle;
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

	public void setRunning() {
		// TODO Auto-generated method stub
		if(state == StateStage2.Idle || state == StateStage2.Blocked)
			state = StateStage2.Running;
		else {
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				e.printStackTrace();
				System.out.println("\t Cannot change the state of a machine of stage 2 to Running with the state " + state);
				System.exit(1);
			}
		}
	}

	public StateStage2 getState() {
		// TODO Auto-generated method stub
		return state;
	}

	public void setIdle() {
		// TODO Auto-generated method stub
		if(state == StateStage2.Running)
			state = StateStage2.Idle;
		else {
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				e.printStackTrace();
				System.out.println("\t Cannot change the state of a machine of stage 2 to Idle with the state " + state);
				System.exit(1);
			}
		}
	}

	public void setBlocked() {
		// TODO Auto-generated method stub
		if(state == StateStage2.Running)
			state = StateStage2.Blocked;
		else {
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				e.printStackTrace();
				System.out.println("\t Cannot change the state of a machine of stage 2 to Blocked with the state " + state);
				System.exit(1);
			}
		}
	}
}
