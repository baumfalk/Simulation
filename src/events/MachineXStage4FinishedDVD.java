package events;

import machines.MachineStage4;
import simulation.Simulation;
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
		if(m.leftBuffer().isEmpty())
		{
			m.state = StateStage4.Idle;
			// machines blocked?
			
		}
		
		
		m.removeDVD();
		
		m.addDVD(m.leftBuffer().removeFromBuffer());
		int processingTime = m.generateProcessingTime();
		int machineFinishedTime = sim.getCurrentTime() + processingTime; 
		Event stage4finished =  new MachineXStage4FinishedDVD(machineFinishedTime, machineNumber);
		sim.addToEventQueue(stage4finished);
	}

}
