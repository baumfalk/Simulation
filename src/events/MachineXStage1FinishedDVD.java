package events;

import machines.MachineStageOne;
import machines.MachineStageTwo;
import misc.DVD;
import simulation.Simulation;
import states.StateStageOne;
import states.StateStageTwo;

public class MachineXStage1FinishedDVD extends MachineXEvent {
	public MachineXStage1FinishedDVD(int t, int m, DVD d, int p) {
		super(t, m);
	
		finishedDVD = d;
		procTime = p;
	}

	private final DVD finishedDVD;
	private final int procTime;

	public DVD getFinishedDVD() {
		return finishedDVD;
	}

	public int getProcTime() {
		return procTime;
	}

	@Override
	public void execute(Simulation sim) {
		System.out.println("\t Looking at Stage 1, machine " + machineNumber);
		MachineStageOne m = sim.getMachineStage1(machineNumber);
		switch(m.state)
		{
		case Running:
			int machineProcTime = m.generateProcessingTime();
			int machineFinishedTime = machineProcTime + sim.getCurrentTime();
			int machine2Number = (m.machineNumber <= 2) ? 1 : 2;
			MachineStageTwo m2 =sim.getMachineStage2(machine2Number);
			
			// directly feed the dvd into machine two
			if(m2.state == StateStageTwo.Idle){
				System.out.println("\t Reactivating machine " +m2.machineNumber + " at stage 2!");
				m2.state = StateStageTwo.Running;
				int machineProcTimeM2 = m2.generateProcessingTime(); 
				int machineFinishedTimeM2 = machineProcTimeM2 + sim.getCurrentTime();
				Event event_m2 = new MachineXStage2FinishedDVD(machineFinishedTimeM2, m2.machineNumber, finishedDVD, machineProcTimeM2);
				sim.addToEventQueue(event_m2);
			}
			
			if(m.rightBuffer.size() == sim.maxBufferSize)
			{
				m.state = StateStageOne.Idle;
				m.dvdBeingProcessed = finishedDVD;
				m.processingTimeLeft = 0;
				m.totalProcessingTime = procTime;
				//TODO: statistics for idle time
				System.out.println("\t Buffer next to Stage 1, machine " + m.machineNumber +" is full!");
			} else {
				m.rightBuffer.add(finishedDVD);
				DVD dvd = new DVD(sim.getCurrentTime());
				Event machinestage1 = new MachineXStage1FinishedDVD(machineFinishedTime, m.machineNumber,dvd, machineProcTime);
				sim.addToEventQueue(machinestage1);
				System.out.println("\t DVD successfully processed in Stage 1, machine " +machineNumber);
			}
			break;
		
		// no repair has taken place before:
		// jf-----br------if-----
		// |-------|------|-----
		case Broken:
			m.state = StateStageOne.BrokenAndDVDBeforeRepair;
			int timeSupposedlyFinished = timeOfOccurence;
			int timeCrashed = m.getLastBreakDownTime();
			int processingTimeLeft = timeSupposedlyFinished-timeCrashed;
			System.out.println("\t Machine broken, DVD stuck! Time still needed in machine: " + processingTimeLeft);
			// machine is broken, DVD is stuck in machine!
			
			m.processingTimeLeft = processingTimeLeft;
			m.totalProcessingTime = procTime;
			m.dvdBeingProcessed = finishedDVD;
			break;
		// repair has taken place before:
		// jf-----br------r-----jf
		// |-------|------|-----|
		// time between br and r has to be done again
		case BrokenAndRepairedBeforeDVD:
			System.out.println("\t Machine " + m.machineNumber + " broke down and was repaired before it could finish it's dvd. Rescheduling.");
			m.state = StateStageOne.Running;
			int timeFalselyRun = m.lastRepairTime - m.lastBreakDownTime;
			m.lastRepairTime = m.lastBreakDownTime = -1;
			int newFinishTime = sim.getCurrentTime() + timeFalselyRun;
			Event newEvent = new MachineXStage1FinishedDVD(newFinishTime, m.machineNumber, finishedDVD, procTime);
			sim.addToEventQueue(newEvent);
			break;
		default:
			break;
		}
	}

	
}
