package events;

import exceptions.InvalidStateException;
import machines.ConveyorBelt;
import machines.MachineStage1;
import machines.MachineStage2;
import misc.DVD;
import simulation.Simulation;
import states.StateConveyorBelt;
import states.StateStage1;

public class Stage2Finished extends MachineXEvent {
	
	private MachineStage2 machineStageTwo ;
	private ConveyorBelt conveyorBelt;
	private MachineStage1 firstMachineStage1;
	private MachineStage1 secondMachineStage1;
	private boolean brokeDVD;
	
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
			/*
			 * The machine is happily running, so we can continue normally. 
			 * We need to do the following:
			 * 	1. if the conveyor belt is Blocked
			 * 		a) set the state of this machine to Blocked
			 *  2. if the conveyor belt is not Blocked (i.e. Running or Idle)
			 *  	a) if the conveyor belt is Idle
			 *  		i) Set the state of the conveyor belt to Running
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
			 *  		v) if the lower machine from stage 1 connected to this stage is Blocked
			 *  			I) Set the stage of the lower machine to Running
			 *  			II) Schedule a new Stage1Finished event 
			 */
			conveyorBelt = sim.getConveyorBelt(machineNumber);
			if(conveyorBelt.getState() == StateConveyorBelt.Blocked) {
				machineStageTwo.setBlocked();
			} else {
				if(conveyorBelt.getState() == StateConveyorBelt.Idle) {
					machineStageTwo.setRunning();
				}
				DVD oldDVD = machineStageTwo.removeDVD();
				conveyorBelt.addDVD(oldDVD, timeOfOccurrence);
				sim.scheduleCBFinishedEvent(machineNumber, conveyorBelt.generateProcessingTime(), scheduledBy());
				
				if(machineStageTwo.leftBuffer().isEmpty()) {
					machineStageTwo.setIdle();
					machineStageTwo.setIdleTime(timeOfOccurrence);
				} else {
					DVD newDVD = machineStageTwo.leftBuffer().removeFromBuffer();
					machineStageTwo.addDVD(newDVD);
					
					// iii en further.
				}
			}
			break;
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
	
	@Override
	protected void scheduleEvents(Simulation sim) {
		// TODO Auto-generated method stub
		machineStageTwo = sim.getMachineStage2(machineNumber);
		conveyorBelt = sim.getConveyorBelt(machineStageTwo.machineNumber);
		firstMachineStage1 = sim.getMachineStage1(machineStageTwo.machineNumber*2-1);
		secondMachineStage1 = sim.getMachineStage1(machineStageTwo.machineNumber*2);
		brokeDVD = machineStageTwo.breakDVD();
		switch(machineStageTwo.getState()) {
		case Running:
			if(brokeDVD) {
				System.out.println("\t Machine " + machineStageTwo.machineNumber + " broke a DVD. :-(");
			} else {
				scheduleEventRunningDVDNotBrokenCase(sim);
			}
			break;
		default:
			crashOnState();
			break;
		}
	}


	private void scheduleEventRunningDVDNotBrokenCase(Simulation sim) {
		// schedule new cb event
		if(conveyorBelt.getState() != StateConveyorBelt.Blocked)
		{
			Event conveyorEvent = new CBFinished(sim.getCurrentTime()+conveyorBelt.generateProcessingTime(),sim.getCurrentTime(), machineStageTwo.machineNumber,this.getClass().getSimpleName());
			sim.addToEventQueue(conveyorEvent);
			
			// schedule new stage 2 event
			if(!machineStageTwo.leftBuffer().isEmpty()) {
				
				//schedule new event for this machine
				int machineProcTime = machineStageTwo.generateProcessingTime(); 
				int machineFinishedTime = machineProcTime + sim.getCurrentTime();
				Event eventStage2FinishedDVD = new Stage2Finished(machineFinishedTime,sim.getCurrentTime(), machineStageTwo.machineNumber,this.getClass().getSimpleName());
				sim.addToEventQueue(eventStage2FinishedDVD);
			
				if(firstMachineStage1.getState() == StateStage1.Blocked) {
					//s1m1.setRunning();
					Event event_s1_m1 = new Stage1Finished(sim.getCurrentTime(),sim.getCurrentTime(),firstMachineStage1.machineNumber,firstMachineStage1.totalProcessingTime,this.getClass().getSimpleName());
					sim.addToEventQueue(event_s1_m1);
					System.out.println("\t Reactivating machine at stage 1");
				}
				if(secondMachineStage1.getState() == StateStage1.Blocked) {
					//s1m2.setRunning();
					Event event_s1_m2 = new Stage1Finished(sim.getCurrentTime(),sim.getCurrentTime(),firstMachineStage1.machineNumber,firstMachineStage1.totalProcessingTime,this.getClass().getSimpleName());
					sim.addToEventQueue(event_s1_m2);
					System.out.println("\t Reactivating machine at stage 1");
				}			
			}
		}
	}

	@Override
	protected void updateMachines(Simulation sim) {
		switch(machineStageTwo.getState()) {
		case Blocked:
			break;
		case Idle:
			break;
		case Running:
			if(machineStageTwo.breakDVD()) {
				System.out.println("\t Machine " + machineStageTwo.machineNumber + " broke a DVD. :-(");
				machineStageTwo.removeDVD();
			}
			else {
				handleUpdateMachineRunningDVDNotBrokenCase(sim);
			}
			break;
		default:
			crashOnState();
			break;
		}
	}


	private void handleUpdateMachineRunningDVDNotBrokenCase(Simulation sim) {
		if(conveyorBelt.getState() == StateConveyorBelt.Blocked)
		{
			machineStageTwo.setBlocked();
		} 
		else
		{
			DVD dvdTemp =  machineStageTwo.removeDVD();
			
			conveyorBelt.addDVD(dvdTemp,sim.getCurrentTime());
			
			if(machineStageTwo.leftBuffer().isEmpty()) {
				machineStageTwo.setIdle();
			}
			// schedule new stage 2 event
			else {
				DVD dvd= machineStageTwo.leftBuffer().removeFromBuffer();
				machineStageTwo.addDVD(dvd);
				if(firstMachineStage1.getState() == StateStage1.Blocked) {
					firstMachineStage1.setRunning();
					System.out.println("\t Reactivating machine at stage 1");
				}
				if(secondMachineStage1.getState() == StateStage1.Blocked) {
					secondMachineStage1.setRunning();
					System.out.println("\t Reactivating machine at stage 1");
				}
			}
			
			if(conveyorBelt.getState() == StateConveyorBelt.Idle) {
				conveyorBelt.setRunning();
			}
		}
	}

	private void crashOnState() {
		try {
			throw new InvalidStateException();
		} catch (InvalidStateException e) {
			
			e.printStackTrace();
			System.out.println("\t State " + machineStageTwo.getState() + " is invalid for the event " + this.getClass().getSimpleName() + "!");
			System.exit(1);
		}
	}
}
