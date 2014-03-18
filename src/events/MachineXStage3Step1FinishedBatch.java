package events;

import machines.MachineStage3;
import simulation.Simulation;

public class MachineXStage3Step1FinishedBatch extends MachineXEvent {

	private MachineStage3 s3m;

	public MachineXStage3Step1FinishedBatch(int t, int tos, int m) {
		super(t, tos,m);
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
		Event eventStage3Step2Finished = new MachineXStage3Step2FinishedBatch(machineFinishedTime,sim.getCurrentTime(), s3m.machineNumber);
		sim.addToEventQueue(eventStage3Step2Finished);
	}

	@Override
	public void updateMachines(Simulation sim) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scheduleEvents(Simulation sim) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateStatistics(Simulation sim) {
		// TODO Auto-generated method stub
		
	}

}
