package events;

import machines.MachineStage3;
import simulation.Simulation;

public class MachineXStage3Step1FinishedBatch extends MachineXEvent {

	private MachineStage3 s3m;

	public MachineXStage3Step1FinishedBatch(int machineFinishedTime,
			int machineNumber) {
		super(machineFinishedTime,machineNumber);
	}

	@Override
	public void execute(Simulation sim) {
		// TODO Auto-generated method stub
		s3m = sim.getMachineStage3(machineNumber);
		
		addStep2Event(sim);
		
	}

	private void addStep2Event(Simulation sim) {
		int processingTimeStep2 = s3m.generateProcessingTimeStep2();
		int machineFinishedTime = sim.getCurrentTime() + processingTimeStep2;
		Event eventStage3Step2Finished = new MachineXStage3Step2FinishedBatch(machineFinishedTime, s3m.machineNumber);
		sim.addToEventQueue(eventStage3Step2Finished);
	}

}
