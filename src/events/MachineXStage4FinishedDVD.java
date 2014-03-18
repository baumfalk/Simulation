package events;

import machines.MachineStage3;
import machines.MachineStage4;
import simulation.Simulation;
import states.StateStage3;
import states.StateStage4;

public class MachineXStage4FinishedDVD extends MachineXEvent {

	public MachineXStage4FinishedDVD(int t, int m) {
		super(t, m);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(Simulation sim) {
		/*
		 * Cases:
		 * 
		 */
		MachineStage4 m = sim.getMachineStage4(machineNumber);
		m.removeDVD();
		if(m.leftBuffer().isEmpty())
		{
			m.state = StateStage4.Idle;
			// machines blocked?
			MachineStage3 s3m1 = sim.getMachineStage3(machineNumber);
			MachineStage3 s3m2 = sim.getMachineStage3(3-machineNumber);
			if(s3m1.state == StateStage3.Blocked) {
				Event stage3finished =  new MachineXStage3Step3FinishedBatch(sim.getCurrentTime(), s3m1.machineNumber);
				sim.addToEventQueue(stage3finished);
			}
			if(s3m1.state == StateStage3.Blocked) {
				Event stage3finished =  new MachineXStage3Step3FinishedBatch(sim.getCurrentTime(), s3m2.machineNumber);
				sim.addToEventQueue(stage3finished);
			}
			
		} else {
			m.addDVD(m.leftBuffer().removeFromBuffer());
			int processingTime = m.generateProcessingTime();
			int machineFinishedTime = sim.getCurrentTime() + processingTime; 
			Event stage4finished =  new MachineXStage4FinishedDVD(machineFinishedTime, machineNumber);
			sim.addToEventQueue(stage4finished);
		}
	}

	@Override
	public void updateMachines(Simulation sim) {
		
	}

	@Override
	public void scheduleEvents(Simulation sim) {
		
	}

	@Override
	public void updateStatistics(Simulation sim) {
		// TODO Auto-generated method stub
		
	}

}