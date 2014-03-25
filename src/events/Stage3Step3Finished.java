package events;

import java.util.ArrayList;

import machines.ConveyorBelt;
import machines.MachineStage3;
import machines.MachineStage4;
import misc.DVD;
import simulation.Simulation;
import states.StateConveyorBelt;
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
		 * 	1. If the first machine of stage 4 is Idle (i.e. the crate is empty)
		 * 		a) Empty this machine into the crate next to the first machine of stage 4
		 * 		b) Set the state of the first machine of stage 4 to Running
		 * 		c) Add the first dvd from the crate to the stage 4 machine.
		 * 		d) Update the statistics concerning idle time of the first machine of stage 4
		 * 		e) Renew cartridge if necessary and get the delay for this
		 * 		f) Calculate the processing time for the stage 4 event
		 * 		g) Schedule a new Stage4Finished event with the processing time and the cartridge renewal time
		 * 		h) If the first conveyor belt is Blocked
		 * 			i) Set the first conveyor belt to Running
		 * 			ii) Calculate the overtime = current time - time that the first conveyor belt got blocked
		 * 			iii) for all DVD's on the belt
		 * 				I) if the time the dvd was put on the belt + processing time <= current time
		 *  				(note that we can use <= and not just <, since we force in our event ordering that CBFinished events
		 *  				 that happen on the same time as Stage3Step3Finished events are always executed
		 *  				 before the Stage3Step3Finished events)
		 * 					- schedule a ConveyorBeltFinished event for this DVD in overtime seconds.
		 * 					  The original  ConveyorBeltFinished event already went by, so we need to reschedule
		 * 				II) else if the time the dvd was put on the belt + processing time > current time
		 * 					- update the overtime for this DVD on the overtime we calculated by step 1.h.ii .
		 * 				
		 * 		i) If the second conveyor belt is blocked
		 * 			i) Do the same as step h, but then for the second conveyor belt
		 * 	2. Else if the second machine of stage 4 is Idle (i.e. the crate is empty)
		 * 		a) Do the same as with step 1, but then for the second machine
		 * 	3. Else if no machine of stage 4 is Idle
		 * 		a) Set this machine to Blocked
		 * 		b) Set the blocked time for the machine
		 */
		
		MachineStage4 nearestMachineStageFour = sim.getMachineStage4(machineNumber);
		MachineStage4 farthestMachineStageFour = sim.getMachineStage4(3-machineNumber);
		ConveyorBelt nearestConveyorBelt = sim.getConveyorBelt(machineNumber);
		ConveyorBelt farthestConveyorBelt = sim.getConveyorBelt(3-machineNumber);
		if(nearestMachineStageFour.getState() == StateStage4.Idle) {
			scheduleStage4Event(sim, nearestMachineStageFour);
			
			scheduleCBEventIfCBIdle(sim, nearestConveyorBelt);
			scheduleCBEventIfCBIdle(sim, farthestConveyorBelt);

		} else if(farthestMachineStageFour.getState() == StateStage4.Idle) {
			scheduleStage4Event(sim, farthestMachineStageFour);
			
			scheduleCBEventIfCBIdle(sim, nearestConveyorBelt);
			scheduleCBEventIfCBIdle(sim, farthestConveyorBelt);
		} else {
			machineStageThree.setBlocked();
			machineStageThree.setTimeBlockedStarted(timeOfOccurrence);
		}
	}

	private void scheduleCBEventIfCBIdle(Simulation sim,ConveyorBelt conveyorBelt) {
		if(conveyorBelt.getState() == StateConveyorBelt.Idle) {
			// sanity check: belt empty
			sim.sanityCheck(conveyorBelt.machineIsEmpty());
			
			conveyorBelt.setRunning();
			int overtime = timeOfOccurrence - conveyorBelt.getBlockedTime();
			
			for(DVD dvd : conveyorBelt.getDVDsOnBelt()) {
				int dvdOfBeltTime = conveyorBelt.getDVDTimeOfEnteringBelt(dvd.id) + conveyorBelt.generateProcessingTime();
				if(dvdOfBeltTime <= timeOfOccurrence) {
					sim.scheduleCBFinishedEvent(machineNumber, overtime, dvd.id, scheduledBy());
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
