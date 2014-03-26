package events;

import machines.MachineStage1;
import simulation.Simulation;
import exceptions.InvalidStateException;

public class Stage1Breakdown extends MachineXEvent {
	
	private MachineStage1 machineStageOne;

	public Stage1Breakdown(int timeOfOccurrence, int timeOfScheduling, int machineNumber,String scheduledBy) {
		super(timeOfOccurrence,timeOfScheduling,machineNumber, scheduledBy);
	
	}

	@Override
	public void execute(Simulation sim) {
		/*
		 * 	We want to be able to ask things of the machine throughout all methods
		 *	of this event.
		 */
		machineStageOne = sim.getMachineStage1(machineNumber);
		
		/*
		 * There is one less Stage1BreakdownEvent in the queue now.
		 */
		sim.decreaseStage1BreakdownEventCounter(machineNumber);
		
		switch(machineStageOne.getState()) {
		case Blocked:
		case Running:
		case BrokenAndRepaired:
			executeRunningOrBlockedCase(sim);
			break;
		case Broken:
		case BrokenAndDVD:
			/*
			 * Cannot happen, since this means that broken was scheduled before it was repaired
			 */
			invalidState();
			break;
		}
	}

	private void executeRunningOrBlockedCase(Simulation sim) {
		/*
		 * The machine was blocked/running when it broke down. The following has to happen
		 * 
		 * 	1. Set the state of the machine to Broken
		 * 	2. Set the time of breakdown in the machine
		 *  3. Schedule a MachineRepairedEvent
		 */
		machineStageOne.setBroken();
		machineStageOne.setTimeOfBreakdown(timeOfOccurrence);
		
		sim.scheduleStage1RepairedEvent(machineNumber, machineStageOne.generateRepairTime(), scheduledBy());
	}
	
	private void invalidState() {
		try {
			throw new InvalidStateException();
		} catch (InvalidStateException e) {
			
			e.printStackTrace();
			System.out.println("\t State " + machineStageOne.getState() + " is invalid for the event " + this.getClass().getSimpleName() + "!");
			System.out.println("\t It was scheduled at " + this.getTimeOfScheduling());
			System.exit(1);
		}
	}
}
