package events;

import machines.MachineStageOne;
import simulation.Simulation;
import states.StateStageOne;

public class MachineXStage1Repaired extends MachineXEvent {

	public MachineXStage1Repaired(int t, int m) {
		super(t, m);
	
	}

	@Override
	public void execute(Simulation sim) {
		// TODO Auto-generated method stub
		MachineStageOne m = sim.getMachineStage1(machineNumber);
		switch(m.state)
		{
		// no repair has taken place before
		// finished dvd
		// so repair can reschedule
		// df-----br------df-----r
		// |------|-------|------|
		//
		case BrokenAndDVDBeforeRepair:
			Event dvdFinishedEvent = new MachineXStage1FinishedDVD(m.processingTimeLeft+sim.getCurrentTime(), m.machineNumber, m.dvdBeingProcessed, m.totalProcessingTime);
			sim.addToEventQueue(dvdFinishedEvent);
			m.state = StateStageOne.Running;
			break;
			
		// repair has taken place before finished dvd
		// no reschedule
		// df-----br------r-----df
		// |------|-------|-----|
		//
		case Broken:
			m.state = StateStageOne.BrokenAndRepairedBeforeDVD;
			m.lastRepairTime = timeOfOccurence;
			break;
		// other cases should not happen
		default:
			
			break;
		}
		//and breakdown
		int breakdownTime = sim.getCurrentTime()+sim.getMachineStage1(machineNumber).generateBreakDownTime();
		int repairTime =  sim.getMachineStage1(machineNumber).generateRepairTime();
		Event machinestage1breakdown = new MachineXStage1Breakdown(breakdownTime, m.machineNumber, repairTime);
		sim.addToEventQueue(machinestage1breakdown);
	}
}
