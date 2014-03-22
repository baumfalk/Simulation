package events;

import machines.MachineStage1;
import machines.MachineStage2;
import misc.DVD;
import simulation.Simulation;
import states.StateStage2;
import exceptions.InvalidStateException;

public class Stage1Finished extends MachineXEvent {
	public Stage1Finished(int t, int tos, int m, int p,String scheduledBy) {
		super(t, tos,m, scheduledBy);
	
		procTime = p;
	}

	private final int procTime;
	private MachineStage1 m;
	private MachineStage2 m2;

	@Override
	protected void scheduleEvents(Simulation sim) {
		
		m = sim.getMachineStage1(machineNumber);
		System.out.println("Machine #"+ machineNumber);
		int machine2Number = (m.machineNumber <= 2) ? 1 : 2;
		m2 = sim.getMachineStage2(machine2Number);
		switch(m.getState()) {
		case Broken:
			System.out.println("\t Broken, so not scheduling anything");
			break;
		case BrokenAndRepairedBeforeDVD:
			// falsely counted the time between breakdown and repair.
			scheduleEventsBrokenAndRepairedCase(sim);
			break;
		case Running:
			scheduleEventsRunningCase(sim);
			break;
		default:
			invalidStateCase();
			break;
		}
	}

	private void scheduleEventsRunningCase(Simulation sim) {
		// stage 2 is idle, so feed the dvd directly to buffer two
		// and then process a new event
		if(m2.getState() == StateStage2.Idle) {
			scheduleStageTwoEvent(sim);
		}
		// if buffer is not full, reschedule a new event
		if(!m.rightBuffer().isFull()) {
			scheduleStageOneEvent(sim);
		}
	}

	private void scheduleStageOneEvent(Simulation sim) {
		int machineProcTime = m.generateProcessingTime();
		int machineFinishedTime = machineProcTime + sim.getCurrentTime();
		
		Event machinestage1 = new Stage1Finished(machineFinishedTime,sim.getCurrentTime(), m.machineNumber, machineProcTime,this.getClass().getSimpleName());
		sim.addToEventQueue(machinestage1);
	}

	private void scheduleStageTwoEvent(Simulation sim) {
		int machineProcTimeM2 = m2.generateProcessingTime(); 
		int machineFinishedTimeM2 = machineProcTimeM2 + sim.getCurrentTime();
		Event stage2Event = new Stage2Finished(machineFinishedTimeM2,sim.getCurrentTime(), m2.machineNumber, machineProcTimeM2,this.getClass().getSimpleName());
		sim.addToEventQueue(stage2Event);
	}

	private void scheduleEventsBrokenAndRepairedCase(Simulation sim) {
		int timeFalselyRun = m.lastRepairTime - m.lastBreakDownTime;
		int newFinishTime = sim.getCurrentTime() + timeFalselyRun;
		Event newEvent = new Stage1Finished(newFinishTime,sim.getCurrentTime(), m.machineNumber, procTime,this.getClass().getSimpleName());
		sim.addToEventQueue(newEvent);
	}
	
	@Override
	protected void updateMachines(Simulation sim) {
		switch (m.getState()) {
	
		case Broken:
			// we would have finished a dvd by now, but we falsely counted
			// the from the breakdown up until the finish time.
			updateMachinesBrokenCase();
			break;
		case BrokenAndRepairedBeforeDVD:
			updateMachinesBrokenAndRepairedCase();
			break;
		case Running:
			updateMachinesRunningCase(sim);
			break;
		default:
			invalidStateCase();
			break;
		}
	}

	private void updateMachinesBrokenCase() {
		m.setBrokenAndDVDBeforeRepair();
	
		int timeSupposedlyFinished = timeOfOccurrence;
		int timeCrashed = m.lastBreakDownTime;
		int processingTimeLeft = timeSupposedlyFinished-timeCrashed;
		
		m.processingTimeLeft = processingTimeLeft;
		m.totalProcessingTime = procTime;
	}

	private void updateMachinesBrokenAndRepairedCase() {
		m.setRunning();
		m.lastRepairTime = m.lastBreakDownTime = -1;
	}

	private void updateMachinesRunningCase(Simulation sim) {
		if(m.rightBuffer().isFull()) {
			m.setBlocked();
			System.out.println("Blocked stage 1 machine " + m.machineNumber + " at time " + sim.getCurrentTime());
			m.processingTimeLeft = 0;
			m.totalProcessingTime = procTime;
		} 
		else if(m2.getState() == StateStage2.Idle) {
			m2.setRunning();
			m2.addDVD(m.removeDVD());
			DVD dvd = new DVD(sim.getCurrentTime());
			m.addDVD(dvd);
		} else {
			m.rightBuffer().addToBuffer(m.removeDVD());
			DVD dvd = new DVD(sim.getCurrentTime());
			m.addDVD(dvd);
		}
	}

	@Override
	protected void updateStatistics(Simulation sim) {
		if(m.rightBuffer().isFull())
		{
			//TODO: statistics for idle time
		} 
	}

	private void invalidStateCase() {
		try {
			throw new InvalidStateException();
		} catch (InvalidStateException e) {
			
			e.printStackTrace();
			System.out.println("\t State " + m.getState() + " is invalid for the event " + this.getClass().getSimpleName() + "!");
			System.out.println("\t It was scheduled at " + this.getTimeOfScheduling());
			System.exit(1);
		}
	}
}
