package events;

import machines.MachineStage1;
import simulation.Simulation;
import states.StateStage1;

public class MachineXStage1Repaired extends MachineXEvent {

	private MachineStage1 m;

	public MachineXStage1Repaired(int t, int m) {
		super(t, m);
	
	}

	@Override
	public void updateMachines(Simulation sim) {
		
		switch(m.state)
		{
		// no repair has taken place before
		// finished dvd
		// so repair can reschedule
		// df-----br------df-----r
		// |------|-------|------|
		//
		case BrokenAndDVDBeforeRepair:
			m.state = StateStage1.Running;
			break;
			
		// repair has taken place before finished dvd
		// no reschedule
		// df-----br------r-----df
		// |------|-------|-----|
		//
		case Broken:
			m.state = StateStage1.BrokenAndRepairedBeforeDVD;
			m.lastRepairTime = timeOfOccurence;
			
			break;
		// other cases should not happen
		default:
			
			break;
		}
	}

	@Override
	public void scheduleEvents(Simulation sim) {
		m = sim.getMachineStage1(machineNumber);
		switch(m.state)
		{
		// no repair has taken place before
		// finished dvd
		// so repair can reschedule
		// df-----br------df-----r
		// |------|-------|------|
		//
		case BrokenAndDVDBeforeRepair:
			Event dvdFinishedEvent = new MachineXStage1FinishedDVD(m.processingTimeLeft+sim.getCurrentTime(), m.machineNumber, m.totalProcessingTime);
			sim.addToEventQueue(dvdFinishedEvent);
			break;
			
		// repair has taken place before finished dvd
		// no reschedule
		// df-----br------r-----df
		// |------|-------|-----|
		//
		case Broken:
			dvdFinishedEvent = new MachineXStage1FinishedDVD(sim.getCurrentTime(), m.machineNumber, m.totalProcessingTime);
			sim.addToEventQueue(dvdFinishedEvent);
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

	@Override
	public void updateStatistics(Simulation sim) {
		// TODO Auto-generated method stub
		
	}
}
