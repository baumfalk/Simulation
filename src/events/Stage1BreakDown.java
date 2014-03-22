package events;

import machines.MachineStage1;
import simulation.Simulation;
import exceptions.InvalidStateException;

public class Stage1BreakDown extends MachineXEvent {
	
	private MachineStage1 m;

	public Stage1BreakDown(int t, int tos, int m, int r,String scheduledBy) {
		super(t,tos,m, scheduledBy);
	
	}

	@Override
	protected void scheduleEvents(Simulation sim) {
		m = sim.getMachineStage1(machineNumber);
		switch(m.getState()) {
		case Blocked:
		case Running:
			int repairTime = m.generateRepairTime();
			Event repairEvent = new Stage1Repaired(sim.getCurrentTime()+repairTime,sim.getCurrentTime(),machineNumber,this.getClass().getSimpleName());
			sim.addToEventQueue(repairEvent);
			break;
		case BrokenAndRepairedBeforeDVD:
			repairTime = m.generateRepairTime();
			repairEvent = new Stage1Repaired(sim.getCurrentTime()+repairTime,sim.getCurrentTime(),machineNumber,this.getClass().getSimpleName());
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
	protected void updateMachines(Simulation sim) {
		switch(m.getState()) {
		case Blocked:
			m.lastBreakDownTime = timeOfOccurrence;
			m.setBrokenAndBlocked();
			break;
		case Running:
			m.setBroken();
			break;
		case BrokenAndRepairedBeforeDVD:
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
	protected void updateStatistics(Simulation sim) {
		// TODO Auto-generated method stub
	}

}
