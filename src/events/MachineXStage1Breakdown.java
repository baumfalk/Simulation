package events;

import machines.MachineStage1;
import simulation.Simulation;
import exceptions.InvalidStateException;

public class MachineXStage1Breakdown extends MachineXEvent {
	
	private MachineStage1 m;

	public MachineXStage1Breakdown(int t, int tos, int m, int r) {
		super(t,tos,m);
	
	}

	@Override
	public void scheduleEvents(Simulation sim) {
		m = sim.getMachineStage1(machineNumber);
		switch(m.getState()) {
		case Blocked:
		case Running:
			int repairTime = m.generateRepairTime();
			Event repairEvent = new MachineXStage1Repaired(sim.getCurrentTime()+repairTime,sim.getCurrentTime(),machineNumber);
			sim.addToEventQueue(repairEvent);
			break;
		default:
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				
				e.printStackTrace();
				System.out.println("\t State " + m.getState() + " is invalid for this event!");
				System.exit(1);
			}
			break;
		}
				
	}

	@Override
	public void updateMachines(Simulation sim) {
		switch(m.getState()) {
		case Blocked:
			m.lastBreakDownTime = timeOfOccurence;
			m.setBrokenAndBlocked();
			break;
		case Running:
			m.setBroken();
			break;
		default:
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				
				e.printStackTrace();
				System.out.println("\t State " + m.getState() + " is invalid for the event " + this.getClass().getSimpleName() + "!");
				System.exit(1);
			}
			break;
		}	
	}
	
	@Override
	public void updateStatistics(Simulation sim) {
		// TODO Auto-generated method stub
	}

}
