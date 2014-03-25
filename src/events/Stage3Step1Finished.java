package events;

import exceptions.InvalidStateException;
import machines.MachineStage3;
import simulation.Simulation;

public class Stage3Step1Finished extends MachineXEvent {

	private MachineStage3 machineStageThree;

	public Stage3Step1Finished(int t, int tos, int m,String scheduledBy) {
		super(t, tos,m, scheduledBy);
	}

	@Override
	public void execute(Simulation sim) {
		/*
		 * 	We want to be able to ask things of the machine throughout all methods
		 *	of this event.
		 */
		machineStageThree = sim.getMachineStage3(machineNumber);
		/*
		 * There is one less Stage3Step1Finished in the queue now.
		 */
		sim.decreaseStage3Step1FinishedEventCounter(machineNumber);
		
		switch(machineStageThree.getState()) {
		case Blocked:
		case Idle:
			/*
			 * These states cannot happen:
			 * 	1. If the machine is Blocked when this event occurs, then either this event was scheduled while Blocked, 
			 * 	  or the machine got blocked in the meantime. 
			 * 		- The first possibility cannot happen since ConveyorBeltFinished only schedules this event when
			 * 		  the machine is Idle and empty
			 * 		- The second possibility cannot happen since only Stage3Step3Finished can set the machine to blocked.
			 * 		  This event cannot occur before the Stage3Step1Finished event.
			 * 	2. The reasoning for Idle is similar to that for blocked
			 * 
			 * So we stop the simulation.
			 */
			invalidState();
			break;
		case Running:
			executeRunningCase(sim);
			break;
		}
		
	}

	private void executeRunningCase(Simulation sim) {
		/*
		 * The machine is still running and we have processed all dvd's in the batch. We now do the following
		 * 	1. Schedule a new event for Stage3Step2Finished.
		 */
		int processingTimeStep2 = machineStageThree.generateProcessingTimeStep2();
		sim.scheduleStage3Step2FinishedEvent(machineNumber, processingTimeStep2, scheduledBy());
	}
	
	private void invalidState() {
		try {
			throw new InvalidStateException();
		} catch (InvalidStateException e) {
			
			e.printStackTrace();
			System.out.println("\t State " + machineStageThree.getState() + " is invalid for the event " + this.getClass().getSimpleName() + "!");
			System.out.println("\t It was scheduled at " + this.getTimeOfScheduling());
			System.exit(1);
		}
	}
	
}
