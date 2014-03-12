package events;

import exceptions.BufferOverflowException;
import exceptions.BufferUnderflowException;
import machines.MachineStageOne;
import machines.MachineStageTwo;
import misc.DVD;
import simulation.Simulation;
import states.StateStageOne;
import states.StateStageTwo;

public class MachineXStage1FinishedDVD extends MachineXEvent {
	public MachineXStage1FinishedDVD(int t, int m, int p) {
		super(t, m);
	
		procTime = p;
	}

	private final int procTime;
	private MachineStageOne m;

	@Override
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
	}

	private void handleRunningState(Simulation sim) {
		int machineProcTime = m.generateProcessingTime();
		int machineFinishedTime = machineProcTime + sim.getCurrentTime();
		int machine2Number = (m.machineNumber <= 2) ? 1 : 2;
		MachineStageTwo m2 =sim.getMachineStage2(machine2Number);
		
		// directly feed the dvd into machine two
		if(m2.state == StateStageTwo.Idle){
			handleStage2Idle(sim);
		}
		else {
			if(m.rightBuffer().isFull())
			{
				m.state = StateStageOne.Blocked;
				m.processingTimeLeft = 0;
				m.totalProcessingTime = procTime;
				//TODO: statistics for idle time
				System.out.println("\t Buffer next to Stage 1, machine " + m.machineNumber +" is full!");
			} else {
				try {
					m.rightBuffer().addToBuffer(m.removeDVD());
				} catch (BufferUnderflowException e) {
					e.printStackTrace();
					System.exit(1);
				} catch (BufferOverflowException e) {
					e.printStackTrace();
					System.exit(1);
				}
				
				DVD dvd = new DVD(sim.getCurrentTime());
				try {
					m.addDVD(dvd);
				} catch (BufferOverflowException e) {
					e.printStackTrace();
					System.exit(1);
				}
				Event machinestage1 = new MachineXStage1FinishedDVD(machineFinishedTime, m.machineNumber, machineProcTime);
				sim.addToEventQueue(machinestage1);
				System.out.println("\t DVD successfully processed in Stage 1, machine " +machineNumber);
			}
		}
	}
	
	private void handleStage2Idle(Simulation sim)
	{
		int machine2Number = (m.machineNumber <= 2) ? 1 : 2;
		MachineStageTwo m2 =sim.getMachineStage2(machine2Number);
		System.out.println("\t Reactivating machine " +m2.machineNumber + " at stage 2!");
		m2.state = StateStageTwo.Running;
		try {
			m2.addDVD(m.removeDVD());
		} catch (BufferUnderflowException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (BufferOverflowException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		int machineProcTimeM2 = m2.generateProcessingTime(); 
		int machineFinishedTimeM2 = machineProcTimeM2 + sim.getCurrentTime();
		Event event_m2 = new MachineXStage2FinishedDVD(machineFinishedTimeM2, m2.machineNumber, machineProcTimeM2);
		sim.addToEventQueue(event_m2);
	}
	
	private void handleBrokenState(Simulation sim) {
		m.state = StateStageOne.BrokenAndDVDBeforeRepair;
		int timeSupposedlyFinished = timeOfOccurence;
		int timeCrashed = m.lastBreakDownTime;
		int processingTimeLeft = timeSupposedlyFinished-timeCrashed;
		System.out.println("\t Machine broken, DVD stuck! Time still needed in machine: " + processingTimeLeft);
		// machine is broken, DVD is stuck in machine!
		
		m.processingTimeLeft = processingTimeLeft;
		m.totalProcessingTime = procTime;
	}
	private void handleBrokenAndRepairedState(Simulation sim) {
		System.out.println("\t Machine " + m.machineNumber + " broke down and was repaired before it could finish it's dvd. Rescheduling.");
		m.state = StateStageOne.Running;
		int timeFalselyRun = m.lastRepairTime - m.lastBreakDownTime;
		m.lastRepairTime = m.lastBreakDownTime = -1;
		int newFinishTime = sim.getCurrentTime() + timeFalselyRun;
		Event newEvent = new MachineXStage1FinishedDVD(newFinishTime, m.machineNumber, procTime);
		sim.addToEventQueue(newEvent);
	}
}
