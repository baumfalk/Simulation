package events;

import java.util.ArrayList;

import machines.ConveyorBelt;
import machines.MachineStage2;
import machines.MachineStage3;
import machines.MachineStage4;
import misc.DVD;
import simulation.Simulation;
import states.StateConveyorBelt;
import states.StateStage2;
import states.StateStage4;
import exceptions.InvalidStateException;

public class Stage3Step3Finished extends MachineXEvent {

	private MachineStage3 machineStageThree;

	public Stage3Step3Finished(int t, int tos, int m,String scheduledBy) {
		super(t, tos, m, scheduledBy);
	}

	@Override
	public void execute(Simulation sim) {
		/*
		 * 	We want to be able to ask things of the machine throughout all methods
		 *	of this event.
		 */
		machineStageThree = sim.getMachineStage3(machineNumber);
		/*
		 * There is one less Stage3Step3Finished in the queue now.
		 */
		sim.decreaseStage3Step3FinishedEventCounter(machineNumber);
		
		switch(machineStageThree.getState()) {
		case Blocked:
		case Idle:
			/* These states cannot happen:
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
		 * We finished a whole batch of DVD's We need to do the following
		 * 	1. If the nearest machine of stage 4 is Idle (i.e. the crate is empty)
		 * 		a) Empty this machine into the crate next to the nearest machine of stage 4
		 * 		b) Set the state of the nearest machine of stage 4 to Running
		 * 		c) Add the nearest dvd from the crate to the stage 4 machine.
		 * 		d) Update the statistics concerning idle time of the nearest machine of stage 4
		 * 		e) Renew cartridge if necessary and get the delay for this
		 * 		f) Calculate the processing time for the stage 4 event
		 * 		g) Schedule a new Stage4Finished event with the processing time and the cartridge renewal time
		 * 		
		 * 		h) If the nearest left buffer is full
		 * 			i) Empty the buffer into this machine
		 * 			ii) Schedule a new Stage3Step1FinishedEvent
		 * 			iii) If the nearest conveyor belt is Blocked
		 * 				I) Set the nearest conveyor belt to Running
		 * 				II) Calculate the overtime = current time - time that the nearest conveyor belt got blocked
		 * 				III) for all DVD's on the belt
		 * 					- if the time the dvd was put on the belt + processing time <= current time
		 *  				  (note that we can use <= and not just <, since we force in our event ordering that CBFinished events
		 *  				  that happen on the same time as Stage3Step3Finished events are always executed
		 *  				  before the Stage3Step3Finished events)
		 *  					* calculate how many seconds the dvd should still be on the belt
		 * 						* schedule a ConveyorBeltFinished event for this DVD in timeleft seconds.
		 * 					  	  The original  ConveyorBeltFinished event already went by, so we need to reschedule
		 * 					- else if the time the dvd was put on the belt + processing time > current time
		 * 						* update the overtime for this DVD on the overtime we calculated by step 1.h.ii .
		 * 				
		 * 		l) Else if the farthest left buffer is full
		 * 			i) Do the the same thing as with j, but then for the farthest buffer.
		 * 		m) If both buffers are empty
		 * 			i) Set the machine on Idle
		 * 			ii) Update the idle time for this machine
		 * 	2. Else if the farthest machine of stage 4 is Idle (i.e. the crate is empty)
		 * 		a) Do the same as with step 1, but then for the farthest machine
		 * 	3. Else if no machine of stage 4 is Idle
		 * 		a) Set this machine to Blocked
		 * 		b) Set the blocked time for the machine
		 */
		
		int nearestNumber = machineNumber;
		int farthestNumber = 3 - machineNumber;
		MachineStage4 nearestMachineStageFour = sim.getMachineStage4(nearestNumber);
		MachineStage4 farthestMachineStageFour = sim.getMachineStage4(farthestNumber);
	
		
		if(nearestMachineStageFour.getState() == StateStage4.Idle) {
			scheduleStage4Event(sim, nearestMachineStageFour);
			
			handleFullLeftBuffer(sim, nearestNumber, farthestNumber);
			

		} else if(farthestMachineStageFour.getState() == StateStage4.Idle) {
			scheduleStage4Event(sim, farthestMachineStageFour);
			
			handleFullLeftBuffer(sim, nearestNumber, farthestNumber);
		} else {
			machineStageThree.setBlocked();
			machineStageThree.setTimeBlockedStarted(timeOfOccurrence);
		}
	}

	private void handleFullLeftBuffer(Simulation sim, int nearestNumber,
			int farthestNumber) {
		if(machineStageThree.leftBuffer(nearestNumber).isFull()) {
			scheduleStageThreeStepOneEvent(sim, nearestNumber);
		} else if(machineStageThree.leftBuffer(farthestNumber).isFull()) {
			scheduleStageThreeStepOneEvent(sim, farthestNumber);
		} else {
			machineStageThree.setIdle();
			machineStageThree.setTimeIdleStarted(timeOfOccurrence);
		}
	}

	private void scheduleStageThreeStepOneEvent(Simulation sim,	int nearestNumber) {
		machineStageThree.addBatch(machineStageThree.leftBuffer(nearestNumber).emptyBuffer());
		
		int processingTime = machineStageThree.generateProcessingTimeStep1();
		sim.scheduleStage3Step1FinishedEvent(machineNumber, processingTime, scheduledBy());
		ConveyorBelt nearestConveyorBelt = sim.getConveyorBelt(nearestNumber);
		scheduleCBEventIfCBBlocked(sim, nearestConveyorBelt);
	}

	private void scheduleCBEventIfCBBlocked(Simulation sim,ConveyorBelt conveyorBelt) {
		if(conveyorBelt.getState() == StateConveyorBelt.Blocked) {
			MachineStage2 machineStageTwo = sim.getMachineStage2(conveyorBelt.machineNumber);
			// sanity check: belt not empty
			sim.sanityCheck(!conveyorBelt.machineIsEmpty());
			conveyorBelt.setRunning();
			
			// Update statistics on idle time of the conveyor belt
			int totalBlockedTimeConveyorBelt = timeOfOccurrence-conveyorBelt.getBlockedTime();
			sim.statistics.addToStatistic("Conveyor Belt Machine "+ conveyorBelt.machineNumber + " blocked time", totalBlockedTimeConveyorBelt);
			
			int totalBlockedTimeStage2 = timeOfOccurrence-machineStageTwo.getBlockedTime();
			sim.statistics.addToStatistic("Stage2 Machine "+ machineStageTwo.machineNumber + " blocked time", totalBlockedTimeStage2);
			
			if(machineStageTwo.getState() == StateStage2.Blocked) {
				machineStageTwo.setRunning();
				sim.scheduleStage2FinishedEvent(conveyorBelt.machineNumber, 0, scheduledBy());
			}
			int overtime = timeOfOccurrence - conveyorBelt.getBlockedTime();
			
			for(DVD dvd : conveyorBelt.getDVDsOnBelt()) {
				int dvdOffBeltTime = conveyorBelt.getDVDTimeOfEnteringBelt(dvd.id) + conveyorBelt.generateProcessingTime();
				int timeProcessed = conveyorBelt.getBlockedTime() - conveyorBelt.getDVDTimeOfEnteringBelt(dvd.id);
				int timeLeft =  conveyorBelt.generateProcessingTime() - timeProcessed;
				if(dvdOffBeltTime <= timeOfOccurrence) {
					sim.scheduleCBFinishedEvent(conveyorBelt.machineNumber, timeLeft, dvd.id, scheduledBy());
				} else {
					int newOvertime = conveyorBelt.getDVDOvertime(dvd.id) + overtime; 
					conveyorBelt.setDVDOvertime(dvd.id, newOvertime);
				}
			}
		}
	}

	private void scheduleStage4Event(Simulation sim, MachineStage4 machineStageFour) {
		// sanity check: the buffer has to be empty
		sim.sanityCheck(machineStageFour.leftBuffer().isEmpty()) ;
		
		ArrayList<DVD> outputFromMachine = machineStageThree.removeBatch();
		
		machineStageFour.leftBuffer().addBatchToBuffer(outputFromMachine);
		
		// sanity check: the buffer is now full
		sim.sanityCheck(machineStageFour.leftBuffer().isFull());

		machineStageFour.setRunning();
		
		machineStageFour.addDVD(machineStageFour.leftBuffer().removeFromBuffer());
		
		// Update statistics on idle time of stage 4 machine
		int totalIdleTime = timeOfOccurrence-machineStageFour.getIdleTime();
		sim.statistics.addToStatistic("Stage 4 Machine "+ machineStageFour.machineNumber + " idle time", totalIdleTime);
		
		int delay = machineStageFour.renewCartridgeIfNecessary();
		int processingTimeStageFour = machineStageFour.generateProcessingTime() + delay;

		sim.scheduleStage4Finished(machineStageFour.machineNumber, processingTimeStageFour, scheduledBy());
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
