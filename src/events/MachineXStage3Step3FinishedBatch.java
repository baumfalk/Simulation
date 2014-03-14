package events;

import machines.MachineStage3;
import simulation.Simulation;
import states.StateStage3;

public class MachineXStage3Step3FinishedBatch extends MachineXEvent {

	public MachineXStage3Step3FinishedBatch(int t, int m) {
		super(t, m);
	}

	@Override
	public void execute(Simulation sim) {
		// TODO Auto-generated method stub
		addStage4Event(sim);
	}
	private void addStage4Event(Simulation sim) {
		MachineStage3 m = sim.getMachineStage3(machineNumber);
		if (m.rightBuffer(machineNumber - 1).isEmpty()) {
			m.rightBuffer(m.machineNumber - 1).addBatchToBuffer(
			m.removeBatch());
			m.state = StateStage3.Idle;
		} else if (m.rightBuffer(2 - machineNumber).isEmpty()) {
			m.rightBuffer(2 - machineNumber).addBatchToBuffer(m.removeBatch());
			m.state = StateStage3.Idle;
		} else {
			m.state = StateStage3.Blocked;
			System.out.println(" \t Stage 3 machine " + machineNumber
					+ " blocked!");
		}
	}
}
