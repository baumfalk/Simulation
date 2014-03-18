package events;

import exceptions.InvalidStateException;
import machines.MachineStage1;
import simulation.Simulation;
import states.StateStage1;

public class MachineXStage1Breakdown extends MachineXEvent {
	
	private MachineStage1 m;

	public MachineXStage1Breakdown(int t, int m, int r) {
		super(t,m);
	
	}

	@Override
	public void scheduleEvents(Simulation sim) {
		m = sim.getMachineStage1(machineNumber);
		int repairTime = m.generateRepairTime();
		Event repairEvent = new MachineXStage1Repaired(sim.getCurrentTime()+repairTime,machineNumber);
		sim.addToEventQueue(repairEvent);		
	}

	@Override
	public void updateMachines(Simulation sim) {
		m.lastBreakDownTime = timeOfOccurence;
		if(m.isStateX(StateStage1.Running))
			m.setBroken();
		else if(m.isStateX(StateStage1.Blocked))
			m.setBrokenAndBlocked();
		else
		{
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				e.printStackTrace();
				System.exit(1);
			}
		
		}
			
	}

	
	@Override
	public void updateStatistics(Simulation sim) {
		// TODO Auto-generated method stub
	}

}
