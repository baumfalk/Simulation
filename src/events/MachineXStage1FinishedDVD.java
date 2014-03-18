package events;

import exceptions.InvalidStateException;
import machines.MachineStage1;
import machines.MachineStage2;
import misc.DVD;
import simulation.Simulation;
import states.StateStage1;
import states.StateStage2;

public class MachineXStage1FinishedDVD extends MachineXEvent {
	public MachineXStage1FinishedDVD(int t, int tos, int m, int p) {
		super(t, tos,m);
	
		procTime = p;
	}

	private final int procTime;
	private MachineStage1 m;
	private MachineStage2 m2;



	private void scheduleNewStageOneEvent(Simulation sim) {
		int machineProcTime = m.generateProcessingTime();
		int machineFinishedTime = machineProcTime + sim.getCurrentTime();
		
		Event machinestage1 = new MachineXStage1FinishedDVD(machineFinishedTime,sim.getCurrentTime(), m.machineNumber, machineProcTime);
		sim.addToEventQueue(machinestage1);
	}
	
	
	


	@Override
	public void scheduleEvents(Simulation sim) {
		
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
			int timeFalselyRun = m.lastRepairTime - m.lastBreakDownTime;
			m.lastRepairTime = m.lastBreakDownTime = -1;
			int newFinishTime = sim.getCurrentTime() + timeFalselyRun;
			Event newEvent = new MachineXStage1FinishedDVD(newFinishTime,sim.getCurrentTime(), m.machineNumber, procTime);
			sim.addToEventQueue(newEvent);
			break;
		case Running:
			if(m2.isIdle()) {
				int machineProcTimeM2 = m2.generateProcessingTime(); 
				int machineFinishedTimeM2 = machineProcTimeM2 + sim.getCurrentTime();
				Event stage2Event = new MachineXStage2FinishedDVD(machineFinishedTimeM2,sim.getCurrentTime(), m2.machineNumber, machineProcTimeM2);
				sim.addToEventQueue(stage2Event);
				scheduleNewStageOneEvent(sim);
			}
			// if buffer is not full
			else if(m.rightBuffer().currentDVDCount() < m.rightBuffer().maxSize-1 ) {
				scheduleNewStageOneEvent(sim);
			}
			break;
		default:
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				
				e.printStackTrace();
				System.out.println("\t State " + m.getState() + " is invalid for the event " + this.getClass().getSimpleName() + "!");
				System.out.println("\t It was scheduled at " + this.getTimeOfScheduling());
				System.exit(1);
			}
			break;
		}
	}
	
	@Override
	public void updateMachines(Simulation sim) {
		
		switch (m.getState()) {
	
		case Broken:
			// we would have finished a dvd by now, but we falsely counted
			// the from the breakdown up until the finish time.
			m.setBrokenAndDVDBeforeRepair();
		
			int timeSupposedlyFinished = timeOfOccurence;
			int timeCrashed = m.lastBreakDownTime;
			int processingTimeLeft = timeSupposedlyFinished-timeCrashed;
			
			m.processingTimeLeft = processingTimeLeft;
			m.totalProcessingTime = procTime;
			break;
		case BrokenAndRepairedBeforeDVD:
			m.setRunning();
			
			m.lastRepairTime = m.lastBreakDownTime = -1;
			break;
		case Running:
			if(m.rightBuffer().isFull()) {
				m.setBlocked();
				
				m.processingTimeLeft = 0;
				m.totalProcessingTime = procTime;
			} 
			else if(m2.isIdle()) {
				m2.setRunning();
				m2.addDVD(m.removeDVD());
				DVD dvd = new DVD(sim.getCurrentTime());
				m.addDVD(dvd);
			} else {
				// we add a new dvd
				m.rightBuffer().addToBuffer(m.removeDVD());
				DVD dvd = new DVD(sim.getCurrentTime());
				m.addDVD(dvd);
			}
			
			break;
		default:
			try {
				throw new InvalidStateException();
			} catch (InvalidStateException e) {
				
				e.printStackTrace();
				System.out.println("\t State " + m.getState() + " is invalid for the event " + this.getClass().getSimpleName() + "!");
				System.exit(1);
			}
			break;
		}
	}

	@Override
	public void updateStatistics(Simulation sim) {
		if(m.rightBuffer().isFull())
		{
			//TODO: statistics for idle time
		} 
	}
}
