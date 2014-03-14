package events;

import exceptions.BufferOverflowException;
import machines.MachineStage3;
import simulation.Simulation;
import states.StateStage3;

public class MachineXStage3Step1FinishedBatch extends MachineXEvent {

	private MachineStage3 m;

	public MachineXStage3Step1FinishedBatch(int machineFinishedTime,
			int machineNumber) {
		super(machineFinishedTime,machineNumber);
	}

	@Override
	public void execute(Simulation sim) {
		// TODO Auto-generated method stub
		m = sim.getMachineStage3(machineNumber);
		
		if(m.rightBuffer(machineNumber-1).isEmpty()) {
			try {
				m.rightBuffer(m.machineNumber-1).addBatchToBuffer(m.removeBatch());
			} catch (BufferOverflowException e) {
				e.printStackTrace();
				System.exit(1);
			}
			m.state = StateStage3.Idle;
		} else if( m.rightBuffer(2-machineNumber).isEmpty()){
			try {
				m.rightBuffer(2-machineNumber).addBatchToBuffer(m.removeBatch());
			} catch (BufferOverflowException e) {
				e.printStackTrace();
				System.exit(1);
			}
			m.state = StateStage3.Idle;
		} else 
		{
			m.state = StateStage3.Blocked;
			System.out.println(" \t Stage 3 machine " + machineNumber + " blocked!");
		}
	}

}
