package events;

import machines.MachineStage3;
import simulation.Simulation;

public class Stage3Step2Finished extends MachineXEvent {

	private MachineStage3 s3m;

	public Stage3Step2Finished(int t, int tos, int m,String scheduledBy) {
		super(t, tos,m, scheduledBy);
	}

	@Override
	public void execute(Simulation sim) {
		addStep3Event(sim);
	}

	private void addStep3Event(Simulation sim) {
		s3m = sim.getMachineStage3(machineNumber);
		int delay = 0;
		for(int i =0; i< s3m.batchSize; i++) {
			if(s3m.machineStuckOnDVD()) {
				delay += s3m.generateRepairTime();
				System.out.println("\tStage 3 step 2 crashed on a dvd!");
			}
		}
		int processingTimeStep3 = s3m.generateProcessingTimeStep3();
		int machineFinishedTime = sim.getCurrentTime() + processingTimeStep3 + delay;
		Event eventStage3Step3Finished = new Stage3Step3Finished(machineFinishedTime,sim.getCurrentTime(), s3m.machineNumber,this.getClass().getSimpleName());
		sim.addToEventQueue(eventStage3Step3Finished);
	}
}
