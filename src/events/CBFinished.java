package events;

import machines.ConveyorBelt;
import machines.MachineStage3;
import misc.DVD;
import simulation.Simulation;
import states.StateStage3;
import exceptions.InvalidStateException;

public class CBFinished extends MachineXEvent {

	public final int dvdID;
	private ConveyorBelt conveyorBelt;
	
	public CBFinished(int t, int tos, int c, int dvdID, String scheduledBy) {
		super(t,tos, c, scheduledBy);
		this.dvdID = dvdID;
	}
		
	@Override
	public void execute(Simulation sim) {
		/*
		 * 	We want to be able to ask things of the machine throughout all methods
		 *	of this event.
		 */
		conveyorBelt = sim.getConveyorBelt(machineNumber);
		
		/*
		 * There is one less CBFinished for this DVD in the queue now.
		 */
		sim.decreaseConveyorBeltFinishedCounter(machineNumber, dvdID);
		
		switch(conveyorBelt.getState()) {
		case Blocked:
			/*
			 * We thought this DVD could pop of the belt, but the belt got blocked in the meantime.
			 * Note that this cannot happen on the first DVD in the queue, since that event would trigger
			 * the conveyor belt to be set to blocked, since the crate to the right is full.
			 * 
			 * We cannot do anything now, except for a sanity check that this dvd is not the first dvd in the queue
			 */
			
			if(conveyorBelt.peekDVD().id == dvdID) {
				sim.crash();
			}
			
			break;
			/*
			 * This should never happen, since this means that either an ConveyorBeltFinished event was scheduled
			 * while the machine was Idle, or that the machine got Idle in the meantime.
			 * 	- The first case cannot happen because only Machine2Finished events will schedule ConveyorBeltFinished events
			 * 	  and set the state to Running if necessary.
			 * 	- The second case cannot happen because only ConveyorBeltFinished can set the machine to Idle 
			 * 	  and it only does so if the belt is empty. 
			 * 
			 * So we stop the simulation.
			 */
		case Idle:
				invalidState();
			break;
		
		case Running:
			executeRunningCase(sim);
			 
			break;
		}
	}

	private void executeRunningCase(Simulation sim) {
		/*
		 * The conveyor belt is running normally. We need to do the following
		 * 	1. If the DVD is really at the end of the belt (i.e. it has been five minutes on a running belt)
		 * 		a) If the buffer/crate to the right is not full
		 * 			i) Remove the dvd from the belt
		 * 			ii) Add the dvd to the crate to the right
		 * 			iii) If the conveyor belt is empty
		 * 				I) Set the conveyor belt state to Idle
		 * 				II) Set the idleTime for the conveyor belt
		 * 			iv) If the buffer is now full
		 * 				I) If the first machine from stage 3 is idle
		 * 					- Empty the buffer into the first machine from stage 3
		 * 					- Set the first machine from stage 3 to Running
		 * 					- Schedule new Stage3Step1FinishedEvent
		 * 					- Update statistics on idle time of the stage 3 machine
		 * 				II) If the second machine from stage 3 is idle
		 * 					- Empty the buffer into the second machine from stage 3
		 * 					- Set the second machine from stage 3 to Running
		 * 					- Schedule new Stage3Step1FinishedEvent
		 * 					- Update statistics on idle time of the second stage 3 machine
		 * 		b) If the buffer to the right is full
		 * 			i) If the first machine from stage 3 is idle
		 * 				I) Empty the buffer into the first machine from stage 3
		 * 				II) Set the first machine from stage 3 to Running
		 * 				III) Schedule new Stage3Step1FinishedEvent
		 * 				IV) Update statistics on idle time of the stage 3 machine
		 * 			ii) Else if the second machine from stage 3 is idle
		 * 				I) Empty the buffer into the second machine from stage 3
		 * 				II) Set the second machine from stage 3 to Running
		 * 				III) Schedule new Stage3Step1FinishedEvent
		 * 				IV) Update statistics on idle time of the second stage 3 machine
		 * 			iii) If both machines are busy
		 * 				I) Set the conveyor belt state to Blocked
		 * 				II) Set the blockedTime for the conveyor belt.
		 * 				III) Update the time left on the belt for all dvd's on the belt.
		 * 	2. If the DVD is not at the end of the belt (i.e. because of a Blocked some time back)
		 * 		a) Schedule a new ConveyorBeltFinished event that will occur in x seconds, 
		 * 		   with x is the time the current DVD has to still be on the belt.
		 * 		b) Set the overtime on the belt for this DVD to 0.
		 * 		 
		 */
		int dvdTimeLeft = conveyorBelt.getDVDOvertime(dvdID);
		if(dvdTimeLeft < 0) {
			sim.crash();
		} else if (dvdTimeLeft == 0) {
			if(!conveyorBelt.rightBuffer().isFull()) {
				DVD dvdFromBelt = conveyorBelt.removeDVD();
				// Error: we cannot have multiple dvd's at the end of the belt.
				if(dvdFromBelt.id != dvdID) {
					sim.crash();
				}
				
				conveyorBelt.rightBuffer().addToBuffer(dvdFromBelt);
				
				if(conveyorBelt.machineIsEmpty()) {
					conveyorBelt.setIdle();
					conveyorBelt.setTimeIdleStarted(timeOfOccurrence);
				}
				
				if(conveyorBelt.rightBuffer().isFull()) {
					MachineStage3 firstMachineStage3 = sim.getMachineStage3(machineNumber);
					MachineStage3 secondMachineStage3 = sim.getMachineStage3(3-machineNumber);
					if(firstMachineStage3.getState() == StateStage3.Idle) {
						scheduleStage3Step1Event(sim, firstMachineStage3);
					}
					else if(secondMachineStage3.getState() == StateStage3.Idle) {
						scheduleStage3Step1Event(sim, secondMachineStage3);
					}
				}
			} else {
				MachineStage3 firstMachineStage3 = sim.getMachineStage3(machineNumber);
				MachineStage3 secondMachineStage3 = sim.getMachineStage3(3-machineNumber);
				if(firstMachineStage3.getState() == StateStage3.Idle) {
					scheduleStage3Step1Event(sim, firstMachineStage3);
				}
				else if(secondMachineStage3.getState() == StateStage3.Idle) {
					scheduleStage3Step1Event(sim, secondMachineStage3);
				} else {
					conveyorBelt.setBlocked();
					conveyorBelt.setTimeBlockedStarted(timeOfOccurrence);
				}
			}
		} else {
			sim.scheduleCBFinishedEvent(machineNumber, dvdTimeLeft , dvdID, scheduledBy());
			conveyorBelt.setDVDOvertime(dvdID, 0);
		}
	}

	private void scheduleStage3Step1Event(Simulation sim,
			MachineStage3 machineStage3) {
		// sanity check: is the machine of stage 3 empty?
		if(!machineStage3.machineIsEmpty()) {
			sim.crash();
		}
		machineStage3.addBatch(conveyorBelt.rightBuffer().emptyBuffer());
		machineStage3.setRunning();
		
		int processingTimeStage3Step1Machine = machineStage3.generateProcessingTimeStep1();
		sim.scheduleStage3Step1FinishedEvent(machineNumber,processingTimeStage3Step1Machine,scheduledBy());
		
		// Update statistics on idle time of stage 3 machine
		int totalIdleTime = timeOfOccurrence-machineStage3.getIdleTime();
		sim.statistics.addToStatistic("Stage 3 Machine "+ machineStage3.machineNumber + " idle time", totalIdleTime);
	}

	
	
	private void invalidState() {
		try {
			throw new InvalidStateException();
		} catch (InvalidStateException e) {
			
			e.printStackTrace();
			System.out.println("\t State " + conveyorBelt.getState() + " is invalid for the event " + this.getClass().getSimpleName() + "!");
			System.out.println("\t It was scheduled at " + this.getTimeOfScheduling());
			System.exit(1);
		}
	}
}
