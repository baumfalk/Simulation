package events;

import exceptions.InvalidStateException;
import machines.MachineStage3;
import machines.MachineStage4;
import misc.DVD;
import simulation.Simulation;
import states.StateStage4;
import buffer.DVDBuffer;

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
			/*
			 * We finished a whole batch of DVD's We need to do the following
			 * 	1. If the first machine of stage 4 is Idle (i.e. the crate is empty)
			 * 		a) Empty this machine into the crate next to the first machine of stage 4
			 * 		b) Set the state of the first machine of stage 4 to Running
			 * 		c) Update the statistics concerning idle time of the first machine of stage 4
			 * 		d) If the cartridge of the first machine of stage 4 needs to be replaced
			 * 			i) Calculate the time needed for this
			 * 		e) Calculate the processing time for the stage 4 event
			 * 		f) Schedule a new Stage4Finished event with the processing time and the cartridge renewal time
			 * 		g) If the first conveyor belt is Blocked
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
			 * 					- set the overtime for this DVD on the overtime we calculated by step 1.g.ii .
			 * 				
			 * 		h) If the second conveyor belt is blocked
			 * 			i) Set the second conveyor belt to Running
			 * 			ii) Calculate the overtime = current time - time that the second conveyor belt got blocked
			 * 			iii) for all DVD's on the belt
			 * 				I) if the time the dvd was put on the belt + processing time <= current time
			 *  				(note that we can use <= and not just <, since we force in our event ordering that CBFinished events
			 *  				 that happen on the same time as Stage3Step3Finished events are always executed
			 *  				 before the Stage3Step3Finished events)
			 * 					- schedule a ConveyorBeltFinished event for this DVD in overtime seconds.
			 * 					  The original  ConveyorBeltFinished event already went by, so we need to reschedule
			 * 				II) else if the time the dvd was put on the belt + processing time > current time
			 * 					- set the overtime for this DVD on the overtime we calculated by step 1.g.ii .
			 * 	2. Else if the second machine of stage 4 is Idle (i.e. the crate is empty)
			 * 	3. If no machine of stage 4 is Idle
			 */
			break;
		}
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
