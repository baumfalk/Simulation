package events;

import machines.MachineStageOne;
import simulation.Simulation;
import states.StateStageOne;

public class MachineXStage1Breakdown extends MachineXEvent {
	
	private final int repairTime;
	
	public MachineXStage1Breakdown(int t, int m, int r) {
		super(t,m);
		repairTime = r;
	
	}

	public int getRepairTime() {
		return repairTime;
	}

	@Override
	public void execute(Simulation sim) {
		MachineStageOne m = sim.getMachineStage1(machineNumber);
		m.lastBreakDownTime = timeOfOccurence;
			
		m.state = StateStageOne.Broken;
	
		Event repairEvent = new MachineXStage1Repaired(sim.getCurrentTime()+repairTime,machineNumber);
		sim.addToEventQueue(repairEvent);
	}

}
