package events;

import machines.ConveyorBelt;
import machines.MachineStage1;
import machines.MachineStage2;
import misc.DVD;
import simulation.Simulation;
import states.StateConveyorBelt;
import states.StateStage1;
import exceptions.InvalidStateException;

public class Stage2Finished extends MachineXEvent {
	
	private MachineStage2 machineStageTwo ;
	private ConveyorBelt conveyorBelt;
	
	public Stage2Finished(int t, int tos, int m,String scheduledBy) {
		super(t, tos,m, scheduledBy);
	}
	
	@Override
	public void execute(Simulation sim) {
		/*
		 * 	We want to be able to ask things of the machine throughout all methods
		 *	of this event.
		 */
		machineStageTwo = sim.getMachineStage2(machineNumber);
		/*
		 * There is one less Stage2FinishedEvent in the queue now.
		 */
		sim.decreaseStage2FinishedEventCounter(machineNumber);
		
		switch(machineStageTwo.getState()) {
		case Blocked:
		case Idle:
			/*
			 * These cases cannot occur, since
			 * 	1. Only Stage1Finished, Stage2Finished and CBFinished can schedule a Stage2Finsihed
			 *  2. If one of those would have scheduled this event, the state would be (changed to) running
			 *  
			 * So we stop the simulation.
			 */
			invalidState();
			break;
		case Running:
			handleRunningCase(sim);
			break;
		}
	}

	private void handleRunningCase(Simulation sim) {
		/*
		 * The machine is happily running, so we can continue normally. 
		 * We need to do the following:
		 * 	1. if the conveyor belt is Blocked
		 * 		a) set the state of this machine to Blocked
		 * 		b) set the blocking time for this machine
		 *  2. if the conveyor belt is not Blocked (i.e. Running or Idle)
		 *  	a) if the conveyor belt is Idle
		 *  		i) Set the state of the conveyor belt to Running
		 *  `		ii) Update Idle statistics for the conveyor belt
		 *  	b) Remove the dvd from the machine
		 *  	c) Add the dvd to the conveyor belt, along with the time of occurrence
		 *  	d) Schedule a new ConveyorBeltFinished event
		 *  	e) If the buffer to the left is empty
		 *  		i) Set the state to Idle
		 *  		ii) Set the idle time in this machine
		 *  	f If the buffer to the left is not empty
		 *  		i) Take a new dvd from the buffer
		 *  		ii) Add the dvd to this machine
		 *  		iii) Schedule a new Stage2Finished Event
		 *  		iv) if the upper machine from stage 1 connected to this stage is Blocked
		 *  			I) Set the stage of the upper machine to Running
		 *  			II) Schedule a new Stage1Finished event 
		 *  			III) Update Blocked statistics for the stage 1 machine
		 *  		v) if the lower machine from stage 1 connected to this stage is Blocked
		 *  			I) Set the stage of the lower machine to Running
		 *  			II) Schedule a new Stage1Finished event 
		 *  			III) Update Blocked statistics for the stage 1 machine
		 */
		conveyorBelt = sim.getConveyorBelt(machineNumber);
		// if the conveyor belt is Blocked, block this machine and set blockedTime of this machine
		if(conveyorBelt.getState() == StateConveyorBelt.Blocked) {
			machineStageTwo.setBlocked();
			machineStageTwo.setTimeBlockedStarted(timeOfOccurrence);
		} else {
			if(conveyorBelt.getState() == StateConveyorBelt.Idle) {
				conveyorBelt.setRunning();
				// Update statistics on idle time of the conveyor belt
				int totalIdleTimeConveyorBelt = timeOfOccurrence-conveyorBelt.getIdleTime();
				sim.statistics.addToStatistic("Conveyor Belt Machine "+ machineNumber + " idle time", totalIdleTimeConveyorBelt);
			}
			DVD oldDVD = machineStageTwo.removeDVD();
			conveyorBelt.addDVD(oldDVD, timeOfOccurrence);
			sim.scheduleCBFinishedEvent(machineNumber, conveyorBelt.generateProcessingTime(), oldDVD.id, scheduledBy());
			
			if(machineStageTwo.leftBuffer().isEmpty()) {
				machineStageTwo.setIdle();
				machineStageTwo.setTimeIdleStarted(timeOfOccurrence);
			} else {
				DVD newDVD = machineStageTwo.leftBuffer().removeFromBuffer();
				machineStageTwo.addDVD(newDVD);
				
				int processingTime = machineStageTwo.generateProcessingTime();
				sim.scheduleStage2FinishedEvent(machineNumber, processingTime, scheduledBy());
				
				int firstMachineStage1Number = (machineNumber*2)-1;
				int secondMachineStage1Number = firstMachineStage1Number + 1;
				
				doIfMachineStage1IsBlocked(sim, firstMachineStage1Number); 
				doIfMachineStage1IsBlocked(sim, secondMachineStage1Number);
			}
		}
	}

	private void doIfMachineStage1IsBlocked(Simulation sim,	int machineStage1Number) {
		MachineStage1 machineStage1 = sim.getMachineStage1(machineStage1Number);
		if(machineStage1.getState() == StateStage1.Blocked) {
			machineStage1.setRunning();
			sim.scheduleStage1FinishedEvent(machineStage1Number, machineStage1.getProcessingTime(), scheduledBy());
			int totalBlockedTimeStage1Machine2 = timeOfOccurrence-machineStage1.getBlockedTime();
			sim.statistics.addToStatistic("Stage 1 Machine "+ machineStage1Number + " blocked time", totalBlockedTimeStage1Machine2);
		}
	}

	private void invalidState() {
		try {
			throw new InvalidStateException();
		} catch (InvalidStateException e) {
			
			e.printStackTrace();
			System.out.println("\t State " + machineStageTwo.getState() + " is invalid for the event " + this.getClass().getSimpleName() + "!");
			System.out.println("\t It was scheduled at " + this.getTimeOfScheduling());
			System.exit(1);
		}
	}

}
