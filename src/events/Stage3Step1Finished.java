package events;

import machines.MachineStage3;
import simulation.Simulation;

public class Stage3Step1Finished extends MachineXEvent {

	private MachineStage3 s3m;

	public Stage3Step1Finished(int t, int tos, int m,String scheduledBy) {
		super(t, tos,m, scheduledBy);
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
		Event eventStage3Step2Finished = new Stage3Step2Finished(machineFinishedTime,sim.getCurrentTime(), s3m.machineNumber,this.getClass().getSimpleName());
		sim.addToEventQueue(eventStage3Step2Finished);
	}
}
