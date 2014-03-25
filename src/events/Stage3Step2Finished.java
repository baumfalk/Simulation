package events;

import exceptions.InvalidStateException;
import machines.MachineStage3;
import simulation.Simulation;

public class Stage3Step2Finished extends MachineXEvent {

	private MachineStage3 machineStageThree;

	public Stage3Step2Finished(int t, int tos, int m,String scheduledBy) {
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
		 * There is one less Stage3Step2Finished in the queue now.
		 */
		sim.decreaseStage3Step2FinishedEventCounter(machineNumber);
		
		switch(machineStageThree.getState()) {
		case Blocked:
		case Idle:
			/*
			 * These states cannot happen:
			 * 	1. If the machine is Blocked when this event occurs, then either this event was scheduled while Blocked, 
			 * 	  or the machine got blocked in the meantime. 
			 * 		- The first possibility cannot happen since then Stage3Step1Finished scheduled this event while that event itself
			 * 		occurred in a Blocked state. The event handler for Stage3Step1Finished terminates the simulation in such a case.
			 * 		- The second possibility cannot happen since only Stage3Step3Finished can set the machine to blocked.
			 * 		  This event cannot occur before the Stage3Step2Finished event.
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
		 * 	1. Calculate the delay caused by crashes of step 2
		 *  2. Calculate the processing time of step 3
		 *  3. Schedule a new Stage3Step3Finished event with the combined time of step 1 and 2
		 */
		int delay = calculateTotalDelayStep2();
		int processingTimeStep3 = machineStageThree.generateProcessingTimeStep3() + delay;
		sim.scheduleStage3Step3FinishedEvent(machineNumber, processingTimeStep3, scheduledBy());
	}

	private int calculateTotalDelayStep2() {
		int delay = 0;
		for(int i =0; i < machineStageThree.batchSize; i++) {
			if(machineStageThree.machineStuckOnDVD()) {
				delay += machineStageThree.generateRepairTime();
				System.out.println("\tStage 3 step 2 crashed on a dvd!");
			}
		}
		return delay;
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
