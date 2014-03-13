package machines;

import java.util.ArrayList;

import exceptions.BufferOverflowException;
import misc.DVD;
import states.StateStage3;
import buffer.Buffer;

public class MachineStage3 extends Machine {

	public StateStage3 state;
	public MachineStage3(int machineNumber, ArrayList<Buffer> leftBuffers,
			ArrayList<Buffer> rightBuffers, int maxDVDInMachine) {
		super(machineNumber, leftBuffers, rightBuffers, maxDVDInMachine);
	}

	@Override
	public int generateProcessingTime() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void addBatch(ArrayList<DVD> batch) {
		
		for(DVD dvd : batch) {
			try {
				this.dvdsInMachine.addToBuffer(dvd);
			} catch (BufferOverflowException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

}
