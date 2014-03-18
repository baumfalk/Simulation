package events;

import machines.MachineStage3;
import simulation.Simulation;

public class MachineXStage3Step2FinishedBatch extends MachineXEvent {

	private MachineStage3 s3m;

	public MachineXStage3Step2FinishedBatch(int t, int tos, int m) {
		super(t, tos,m);
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
				delay += 60*5;
				System.out.println("\tStage 3 step 2 crashed on a dvd!");
			}
		}
		int processingTimeStep3 = s3m.generateProcessingTimeStep3();
		int machineFinishedTime = sim.getCurrentTime() + processingTimeStep3 + delay;
		Event eventStage3Step3Finished = new MachineXStage3Step3FinishedBatch(machineFinishedTime,sim.getCurrentTime(), s3m.machineNumber);
		sim.addToEventQueue(eventStage3Step3Finished);
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
