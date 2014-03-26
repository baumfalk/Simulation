package events;

import machines.MachineStage1;
import misc.DVD;
import simulation.Simulation;
import exceptions.InvalidStateException;

public class Stage1Repaired extends MachineXEvent {

	private MachineStage1 machineStageOne;

	public Stage1Repaired(int t, int tos, int m,String scheduledBy) {
		super(t, tos,m, scheduledBy);
	
	}
	
	@Override
	public void execute(Simulation sim) {
		/*
		 * 	We want to be able to ask things of the machine throughout all methods
		 *	of this event.
		 */
		machineStageOne = sim.getMachineStage1(machineNumber);
		
		/*
		 * There is one less Stage1RepairedEvent in the queue now.
		 */
		sim.decreaseStage1RepairedEventCounter(machineNumber);
		switch(machineStageOne.getState()) {
		case Blocked:
		case BrokenAndRepaired:
		case Running:
			/*
			 * These cases cannot happen, since
			 * 
			 * 	1. Only Stage1Breakdown schedules a Stage1Repair
			 *  2. If the state is not Broken or BrokenAndDVD, the machine had
			 *  to be repaired before this event was executed
			 *  3. That means that there were multiple repair events in the queue for this machine
			 *  
			 *  Since that is illegal, we terminate the simulation now.
			 */
			invalidState();
			break;	
		case Broken:
			executeBrokenCase(sim);
			
			break;
		case BrokenAndDVD:
			setRunningAndScheduleFinishedEvent(sim);
			break;
		}
	}

	private void setRunningAndScheduleFinishedEvent(Simulation sim) {
		/*
		 * The state is BrokenAndDVD, so a Stage1Finished for this machine happened before this event
		 * and while the machine was broken. We must do the following:
		 * 
		 * 	1. Set the state to running
		 *  2. If the machine is empty
		 *  	a) Create a new DVD
		 *  	b) Feed this DVD to the machine
		 *  	c) Generate a processing time for the machine and give it to the machine
		 *  	d) Schedule a new Stage1Finished event
		 */
		machineStageOne.setRunning();
		if(machineStageOne.machineIsEmpty()) {
			DVD newDVD = sim.generateNewDVD();
			machineStageOne.addDVD(newDVD);
			int processingTime = machineStageOne.generateProcessingTime();
			sim.scheduleStage1FinishedEvent(machineNumber, processingTime, scheduledBy());
		}
	}

	private void executeBrokenCase(Simulation sim) {
		/*
		 * The state is Broken, so no Stage1Finished for this machine was scheduled before this event happened.
		 * We must do the following
		 * 
		 *  1. If the machine is currently empty, it was previously blocked. Then, we must
		 *  	a) Set the state to Running
		 *  	b) If the buffer is not full, we must
		 *  		i) Create a new DVD
		 *  		ii) Feed this DVD to the machine
		 *  		iii) Generate a processing time for the machine and give it to the machine
		 *  		iv) Schedule a new Stage1Finished event
		 * 	2. If the machine is not currently empty, it was previously running. Then, we must
		 *  	a) Calculate the time the machine was down and calculate the remaining processing time
		 *  	b) Save the remaining processing time to the machine
		 *  	c) Set the state to BrokenAndRepaired
		 *  3. We must generate a new Stage1Bbrakdown event
		 */
		if(machineStageOne.machineIsEmpty()) {
			setRunningAndScheduleFinishedEvent(sim);
		} else {
			int timeOfBreakdown = machineStageOne.getTimeOfBreakdown();
			int downtime = timeOfScheduling - timeOfBreakdown;
			int processingTimeLeft = machineStageOne.getProcessingTime() - downtime;
			machineStageOne.setProcessingTime(processingTimeLeft);
			machineStageOne.setBrokenAndRepaired();
		}
		sim.scheduleStage1BreakdownEvent(machineNumber, machineStageOne.generateBreakDownTime(), scheduledBy());
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
