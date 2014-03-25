package events;

import machines.MachineStage1;
import machines.MachineStage2;
import misc.DVD;
import simulation.Simulation;
import states.StateStage2;
import exceptions.InvalidStateException;

public class Stage1Finished extends MachineXEvent {
	public Stage1Finished(int supposedFinishingTime, int schedulingTime, int machineNumber, String scheduledBy) {
		super(supposedFinishingTime, schedulingTime,machineNumber, scheduledBy);
	
	}

	private MachineStage1 machineStageOne;
	private MachineStage2 machineStageTwo;

	@Override
	public void execute(Simulation sim) {
		/*
		 * 	We want to be able to ask things of the machine throughout all methods
		 *	of this event.
		 */
		machineStageOne = sim.getMachineStage1(machineNumber);
		/*
		 * There is one less Stage1FinishedEvent in the queue now.
		 */
		sim.decreaseStage1FinishedEventCounter(machineNumber);
		switch(machineStageOne.getState()) {
		case Blocked:
			/*
			 * This state should not be possible when handling a Stage1Finished event, because
			 * 
			 * 	1. Either an Stage1FinishedEvent was scheduled when the machine was already blocked or
			 * 	2. Either the machine became blocked before this Stage1FinishedEvent was handled,
			 * 	   implying that another Stage1FinishedEvent for this machine was already in the queue.
			 * 
			 * Both cases are/should be impossible. So we let the simulation crash.
			 */
			invalidState();
			break;
		case Broken:
			executeBrokenCase();
			break;
		case BrokenAndDVD:
			/*
			 * This state should not be possible when handling a Stage1FinishedEvent, because
			 * 
			 * 	1. Only a Stage1Repaired, Stage1Finished and Stage2Finished can schedule a Stage1Finished
			 *  	a) If the event was a Stage1Repaired, the state would now be Running or Blocked
			 *  	b) If event  was a Stage1Finished or stage2Finished, it would have scheduled this event while still broken.
			 *  	   This is illegal
			 *  All cases are/should be impossible. So we let the simulation crash.
			 */
			invalidState();
			break;
		case BrokenAndRepaired:
			executeBrokenAndRepairedCase(sim);
			break;
		case Running:
			executeRunningCase(sim);
			break;
		}
	}

	private void executeBrokenCase() {
		/*
		 * This machine was running when Stage1Finished was scheduled,
		 * but it broke down in the meantime. We can do the following
		 * 
		 *  1. Set the state to BrokenAndDVDBeforeRepair
		 *  2. Calculate the time the machine did not run, but the simulation thought it ran.
		 *  3. Update the associated values in this machine.
		 */
		machineStageOne.setBrokenAndDVDBeforeRepair();
		
		int timeSupposedlyFinished = timeOfOccurrence;
		int timeOfCrash = machineStageOne.getTimeOfBreakdown();
		int processingTimeLeft = timeSupposedlyFinished - timeOfCrash;
		
		machineStageOne.setProcessingTime(processingTimeLeft);
	}

	private void executeBrokenAndRepairedCase(Simulation sim) {
		/*
		 * The machine broke down and was repaired before this event took place.
		 * We falsely counted some time (namely the time between breakdown and repair) and need to 
		 * reschedule to account for this. We do this as follows.
		 * 
		 * 	1. Set the state to Running
		 * 	2. Schedule a new Stage1FinishedEvent with a processing time of what was falsely counted.
		 */
		machineStageOne.setRunning();
		sim.scheduleStage1FinishedEvent(machineNumber, machineStageOne.getProcessingTime(),scheduledBy());
	}

	private void executeRunningCase(Simulation sim) {
		/*
		 * This machine was running when Stage1Finished was scheduled,
		 * and it did not break down in the meantime. We can do the following
		 * 
		 *  1) if the buffer to the right is not full, we can
		 *  	a) Add the old DVD to the buffer on the right
		 *  	b) Take a new DVD and give it to this machine 
		 *  	c) Schedule a new Stage1FinishedEvent
		 *  	d) Set the processing time in this machine
		 *  	e) If the machine from stage 2 was idle, we can
		 *  		i) Give a dvd (the old dvd) from the buffer to the machine in stage 2
		 *  		ii) Schedule a new Stage2FinishedEvent with time = currentTime 
		 *  		iii) Set the state of the machine in stage 2 to Running
		 *  		iv) Update statistics on idle time of stage 2 machine
		 *  2) if the buffer to the right is full, we can
		 *  	a) Set the state to Blocked
		 *  	b) Set the processing time for this dvd to 0.
		 *  	c) Update the blocked-time statistics for this machine.
		 */ 
		
		if(!machineStageOne.rightBuffer().isFull()) {
			// Add the old DVD to the buffer on the right 
			DVD oldDVD = machineStageOne.removeDVD();
			machineStageOne.rightBuffer().addToBuffer(oldDVD);
			oldDVD = null; // for safety purposes.
			
			// Take a new DVD and give it to this machine
			DVD newDVD = sim.generateNewDVD();
			machineStageOne.addDVD(newDVD);
			
			// Schedule a new Stage1FinishedEvent
			int processingTimeStage1Machine = machineStageOne.generateProcessingTime();
			sim.scheduleStage1FinishedEvent(machineNumber, processingTimeStage1Machine, scheduledBy());
			
			// Set the processing time in this machine
			machineStageOne.setProcessingTime(processingTimeStage1Machine);
			
			
			int machineTwoNumber = 1+(machineNumber-1)/2;
			machineStageTwo = sim.getMachineStage2(machineTwoNumber);
			
			if(machineStageTwo.getState() == StateStage2.Idle) {
				// Give a dvd (the old dvd) from the buffer to the machine in stage 2
				oldDVD = machineStageOne.rightBuffer().removeFromBuffer();
				machineStageTwo.addDVD(oldDVD);
		
				// Schedule a new Stage2FinishedEvent with time = currentTime 
				int processingTimeStage2Machine = machineStageTwo.generateProcessingTime();
				sim.scheduleStage2FinishedEvent(machineTwoNumber, processingTimeStage2Machine, scheduledBy());
				
				//Set the state of the machine in stage 2 to Running
				machineStageTwo.setRunning();
				
				// Update statistics on idle time of stage 2 machine
				int totalIdleTime = timeOfOccurrence-machineStageTwo.getIdleTime();
				sim.statistics.addToStatistic("Stage 2 Machine "+ machineTwoNumber + " idle time", totalIdleTime);
			}
		} else {
			// Set the state to Blocked
			machineStageOne.setBlocked();
			
			machineStageOne.setProcessingTime(0);
			
			// Update the blocked-time statistics for this machine.
			machineStageOne.setTimeBlockedStarted(timeOfOccurrence);
		}
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
