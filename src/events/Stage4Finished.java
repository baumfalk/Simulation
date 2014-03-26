package events;

import machines.MachineStage3;
import machines.MachineStage4;
import misc.DVD;
import simulation.Simulation;
import states.StateStage3;
import exceptions.InvalidStateException;

public class Stage4Finished extends MachineXEvent {

	private MachineStage4 machineStageFour;
	public Stage4Finished(int t, int tos, int m,String scheduledBy) {
		super(t, tos, m, scheduledBy);
	}

	@Override
	public void execute(Simulation sim) {
		/*
		 * 	We want to be able to ask things of the machine throughout all methods
		 *	of this event.
		 */
		machineStageFour = sim.getMachineStage4(machineNumber);
		/*
		 * There is one less Stage3Step1Finished in the queue now.
		 */
		sim.decreaseStage4FinishedEventCounter(machineNumber);
		
		switch(machineStageFour.getState()) {
		case Idle:
			/*
			 * This state cannot happen since then either this event was scheduled while the machine
			 * was Idle, or the machine got Idle in between.
			 * 	- The first case cannot happen since Stage3Step3Finished event would set the state to Running
			 * 	  when scheduling this event.
			 *  - The second case cannot happen since only this event can set the state to idle, and that cannot 
			 *  happen in between.
			 *  
			 */
			invalidState();
			break;
		case Running:
			/*
			 * Our machine is running happily. Do the following.
			 * 
			 * 	1. Remove the DVD from the machine
			 *  2. Update the statistic for this DVD (throughput time, dvd per hour)
			 *  3. If the buffer is not empty
			 *  	a) Take another DVD from the buffer and add it to the machine
			 *  	b) Renew cartridge if necessary and get the delay for this
			 *  	c) Calculate the processing time
			 *  	d) Schedule new Stage4Finished event with processing time + delay as real processing time
			 *  4. If the buffer is empty
			 *  	a) Set the state to Idle
			 *  	b) Set the idle time for this machine
			 *  	c) If the nearest machine from stage 3 is Blocked
			 *  		i) Set the machine from stage 3 to Running
			 *  		ii) Update the statistics for blocked time for this machine
			 *  		iii) Schedule new Stage3Step3Finished event due in 0 seconds.
			 *  	d) If the furthest machine from stage 3 is Blocked
			 *  		i) do the same as in c), but then for the furthest machine.
			 */
			DVD dvdFromMachine = machineStageFour.removeDVD();
			int throughputTime = timeOfOccurrence - dvdFromMachine.timeOfEnteringPipeLine;
			sim.statistics.addToStatistic("Total DVDs processed",1 );
			sim.statistics.updateAverage("Throughput time per DVD",throughputTime);
			
			if(!machineStageFour.leftBuffer().isEmpty()) {
				DVD dvdFromBuffer = machineStageFour.leftBuffer().removeFromBuffer();
				machineStageFour.addDVD(dvdFromBuffer);
				int delay = machineStageFour.renewCartridgeIfNecessary();
				int processingTime = machineStageFour.generateProcessingTime() + delay;

				sim.scheduleStage4Finished(machineNumber, processingTime, scheduledBy());
			} else {
				machineStageFour.setIdle();
				machineStageFour.setTimeIdleStarted(timeOfOccurrence);
				
				MachineStage3 nearestMachineStageThree = sim.getMachineStage3(machineNumber);
				MachineStage3 furthestMachineStageThree = sim.getMachineStage3(3-machineNumber);
				scheduleNewStageThreeStep3Event(sim, nearestMachineStageThree);
				scheduleNewStageThreeStep3Event(sim, furthestMachineStageThree);
			}
			break;
		}
	}

	private void scheduleNewStageThreeStep3Event(Simulation sim,
			MachineStage3 machineStageThree) {
		if(machineStageThree.getState() == StateStage3.Blocked) {
			machineStageThree.setRunning();
			// Update statistics on blocked time of stage 4 machine
			int totalIdleTime = timeOfOccurrence-machineStageThree.getIdleTime();
			sim.statistics.addToStatistic("Stage 3 Machine "+ machineStageFour.machineNumber + " blocked time", totalIdleTime);
			
			sim.scheduleStage3Step3FinishedEvent(machineStageThree.machineNumber, 0, scheduledBy());
		}
	}

	private void invalidState() {
		try {
			throw new InvalidStateException();
		} catch (InvalidStateException e) {
			
			e.printStackTrace();
			System.out.println("\t State " + machineStageFour.getState() + " is invalid for the event " + this.getClass().getSimpleName() + "!");
			System.out.println("\t It was scheduled at " + this.getTimeOfScheduling());
			System.exit(1);
		}
	}

}
