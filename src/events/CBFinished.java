package events;

import machines.ConveyorBelt;
import misc.DVD;
import simulation.Simulation;
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
			break;
		case Idle:
			break;
		
		case Running:
			/*
			 * The conveyor belt is running normally. We need to do the following
			 * 	1. If the DVD is really at the end of the belt (i.e. it has been five minutes on a running belt)
			 * 		a) If the buffer/crate to the right is not full
			 * 			i) Remove the dvd from the belt
			 * 			ii) Add the dvd to the crate to the right
			 * 			iii) If the conveyor belt is empty
			 * 				I) Set the conveyor belt state to Idle
			 * 				II) Set the idleTime for the conveyor belt
			 * 		b) If the buffer to the right is full
			 * 			i) Set the conveyor belt state to Blocked
			 * 			ii) Set the blockedTime for the conveyor belt.
			 * 			iii) Update the time left on the belt for all dvd's on the belt.
			 * 	2. If the DVD is not at the end of the belt (i.e. because of a Blocked some time back)
			 * 		a) Schedule a new ConveyorBeltFinished event that will occur in x seconds, 
			 * 		   with x is the time the current DVD has to still be on the belt.
			 * 		b) Set the time left on the belt for this DVD to 0.
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
				} else {
					conveyorBelt.setBlocked();
					conveyorBelt.setTimeBlockedStarted(timeOfOccurrence);
				}
			} else {
				sim.scheduleCBFinishedEvent(machineNumber, dvdTimeLeft , dvdID, scheduledBy());
				conveyorBelt.setDVDTimeLeft(dvdID, 0);
			}
			 
			break;
		
		}
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
