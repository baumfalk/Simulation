package machines;

import java.util.ArrayList;
import java.util.Arrays;

import exceptions.InvalidStateException;

import states.StateStage2;
import buffer.Buffer;

public class MachineStage2 extends Machine {

	public StateStage2 state;
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

	public boolean isIdle() {
		// TODO Auto-generated method stub
		return state == StateStage2.Idle;
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
}
