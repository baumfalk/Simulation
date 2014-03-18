package events;

import machines.MachineStage1;
import machines.MachineStage2;
import misc.DVD;
import simulation.Simulation;
import states.StateStage1;
import states.StateStage2;

public class MachineXStage1FinishedDVD extends MachineXEvent {
	public MachineXStage1FinishedDVD(int t, int m, int p) {
		super(t, m);
	
		procTime = p;
	}

	private final int procTime;
	private MachineStage1 m;
	private MachineStage2 m2;

	/*@Override
		public void execute(Simulation sim) {
		
		System.out.println("\t Looking at Stage 1, machine " + machineNumber);
		m = sim.getMachineStage1(machineNumber);
		switch(m.state)
		{
		case Running:
			handleRunningState(sim);
			break;
		
		// no repair has taken place before:
		// jf-----br------if-----
		// |-------|------|-----
		case Broken:
			handleBrokenState(sim);
			break;
		// repair has taken place before:
		// jf-----br------r-----jf
		// |-------|------|-----|
		// time between br and r has to be done again
		case BrokenAndRepairedBeforeDVD:
			handleBrokenAndRepairedState(sim);
			break;
		default:
			break;
		}
	}*/

	private void handleRunningState(Simulation sim) {
	
		int machine2Number = (m.machineNumber <= 2) ? 1 : 2;
		MachineStage2 m2 =sim.getMachineStage2(machine2Number);
		
		// directly feed the dvd into machine two
		if(m2.state == StateStage2.Idle){
			handleStage2Idle(sim);
		}
		else {
			if(m.rightBuffer().isFull())
			{
				m.state = StateStage1.Blocked;
				m.processingTimeLeft = 0;
				m.totalProcessingTime = procTime;
				//TODO: statistics for idle time
				System.out.println("\t Buffer next to Stage 1, machine " + m.machineNumber +" is full!");
			} else {
				m.rightBuffer().addToBuffer(m.removeDVD());
				scheduleNewStageOneEvent(sim);
				System.out.println("\t DVD successfully processed in Stage 1, machine " +machineNumber);
			}
		}
	}

	private void scheduleNewStageOneEvent(Simulation sim) {
		int machineProcTime = m.generateProcessingTime();
		int machineFinishedTime = machineProcTime + sim.getCurrentTime();
		
		Event machinestage1 = new MachineXStage1FinishedDVD(machineFinishedTime, m.machineNumber, machineProcTime);
		sim.addToEventQueue(machinestage1);
	}
	
	private void handleStage2Idle(Simulation sim)
	{
		int machine2Number = (m.machineNumber <= 2) ? 1 : 2;
		MachineStage2 m2 =sim.getMachineStage2(machine2Number);
		System.out.println("\t Reactivating machine " +m2.machineNumber + " at stage 2!");
		m2.state = StateStage2.Running;
		m2.addDVD(m.removeDVD());
		int machineProcTimeM2 = m2.generateProcessingTime(); 
		int machineFinishedTimeM2 = machineProcTimeM2 + sim.getCurrentTime();
		Event event_m2 = new MachineXStage2FinishedDVD(machineFinishedTimeM2, m2.machineNumber, machineProcTimeM2);
		sim.addToEventQueue(event_m2);
		scheduleNewStageOneEvent(sim);
	}
	
	private void handleBrokenState(Simulation sim) {
		// we would have finished a dvd by now, but we falsely counted
				// the from the breakdown up until the finish time.
				m.state = StateStage1.BrokenAndDVDBeforeRepair;
				int timeSupposedlyFinished = timeOfOccurence;
				int timeCrashed = m.lastBreakDownTime;
				int processingTimeLeft = timeSupposedlyFinished-timeCrashed;
				
				m.processingTimeLeft = processingTimeLeft;
				m.totalProcessingTime = procTime;
	}
	private void handleBrokenAndRepairedState(Simulation sim) {
		System.out.println("\t Machine " + m.machineNumber + " broke down and was repaired before it could finish it's dvd. Rescheduling.");
		m.state = StateStage1.Running;
		int timeFalselyRun = m.lastRepairTime - m.lastBreakDownTime;
		m.lastRepairTime = m.lastBreakDownTime = -1;
		int newFinishTime = sim.getCurrentTime() + timeFalselyRun;
		Event newEvent = new MachineXStage1FinishedDVD(newFinishTime, m.machineNumber, procTime);
		sim.addToEventQueue(newEvent);
	}

	@Override
	public void scheduleEvents(Simulation sim) {
		m = sim.getMachineStage1(machineNumber);
		int machine2Number = (m.machineNumber <= 2) ? 1 : 2;
		m2 =sim.getMachineStage2(machine2Number);
		if(m.isStateX(StateStage1.BrokenAndRepairedBeforeDVD)) {
			// falsely counted the time between breakdown and repair.
			int timeFalselyRun = m.lastRepairTime - m.lastBreakDownTime;
			m.lastRepairTime = m.lastBreakDownTime = -1;
			int newFinishTime = sim.getCurrentTime() + timeFalselyRun;
			Event newEvent = new MachineXStage1FinishedDVD(newFinishTime, m.machineNumber, procTime);
			sim.addToEventQueue(newEvent);
		} 
		// if stage 2 is idle
		else if(m2.isIdle()) {
			int machineProcTimeM2 = m2.generateProcessingTime(); 
			int machineFinishedTimeM2 = machineProcTimeM2 + sim.getCurrentTime();
			Event stage2Event = new MachineXStage2FinishedDVD(machineFinishedTimeM2, m2.machineNumber, machineProcTimeM2);
			sim.addToEventQueue(stage2Event);
			scheduleNewStageOneEvent(sim);
		}
		// if buffer is not full
		if(!m.rightBuffer().isFull()) {
			// schedule new event
			int machineProcTime = m.generateProcessingTime();
			int machineFinishedTime = machineProcTime + sim.getCurrentTime();
			Event stage1Event = new MachineXStage1FinishedDVD(machineFinishedTime, m.machineNumber, machineProcTime);
			sim.addToEventQueue(stage1Event);
		}
	}
	
	@Override
	public void updateMachines(Simulation sim) {
		switch (m.state) {
		case Blocked:
			// do nothing
			break;
		case Broken:
			// we would have finished a dvd by now, but we falsely counted
			// the from the breakdown up until the finish time.
			m.state = StateStage1.BrokenAndDVDBeforeRepair;
			int timeSupposedlyFinished = timeOfOccurence;
			int timeCrashed = m.lastBreakDownTime;
			int processingTimeLeft = timeSupposedlyFinished-timeCrashed;
			
			m.processingTimeLeft = processingTimeLeft;
			m.totalProcessingTime = procTime;
			break;
		case BrokenAndBlocked:
			// do nothing
			break;
		case BrokenAndDVDBeforeRepair:
			// do nothing
			break;
		case BrokenAndRepairedBeforeDVD:
			// do nothing
			break;
		case Running:
			break;
		default:
			break;

		}
		// if m is broken
		if(m.isStateX(StateStage1.Broken)) {
		
		} else if(m.rightBuffer().isFull()) {
			m.state = StateStage1.Blocked;
			m.processingTimeLeft = 0;
			m.totalProcessingTime = procTime;
		} else {
			if(m2.isIdle()) {
				//m2.addDVD(m.removeDVD());
			} else {
				// we add a new dvd
				m.rightBuffer().addToBuffer(m.removeDVD());
			}
			DVD dvd = new DVD(sim.getCurrentTime());
			m.addDVD(dvd);
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
