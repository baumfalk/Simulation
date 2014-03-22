package events;

import exceptions.InvalidStateException;
import machines.MachineStage1;
import simulation.Simulation;

public class Stage1Repaired extends MachineXEvent {

	private MachineStage1 m;

	public Stage1Repaired(int t, int tos, int m,String scheduledBy) {
		super(t, tos,m, scheduledBy);
	
	}

	@Override
	protected void updateMachines(Simulation sim) {
		
		switch(m.getState())
		{
		// no repair has taken place before
		// finished dvd
		// so repair can reschedule
		// df-----br------df-----r
		// |------|-------|------|
		//
		case BrokenAndDVDBeforeRepair:
			m.setRunning();
			break;
			
		// repair has taken place before finished dvd
		// no reschedule
		// df-----br------r-----df
		// |------|-------|-----|
		//
		case Broken:
			m.brokenAndRepairedBeforeDVD();
			
			m.lastRepairTime = timeOfOccurrence;
			
			break;

		case BrokenAndBlocked:
			m.setBlocked();
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
	protected void scheduleEvents(Simulation sim) {
		m = sim.getMachineStage1(machineNumber);
		switch(m.getState())
		{
		// no repair has taken place before
		// finished dvd
		// so repair can reschedule
		// df-----br------df-----r
		// |------|-------|------|
		//
		case BrokenAndDVDBeforeRepair:
			Event dvdFinishedEvent = new Stage1Finished(m.processingTimeLeft+sim.getCurrentTime(),sim.getCurrentTime(), m.machineNumber, m.totalProcessingTime,this.getClass().getSimpleName());
			sim.addToEventQueue(dvdFinishedEvent);
			break;
			
		// repair has taken place before finished dvd
		// no reschedule
		// df-----br------r-----df
		// |------|-------|-----|
		//
		case Broken:
			dvdFinishedEvent = new Stage1Finished(sim.getCurrentTime(),sim.getCurrentTime(), m.machineNumber, m.totalProcessingTime,this.getClass().getSimpleName());
			sim.addToEventQueue(dvdFinishedEvent);
			break;
		// other cases should not happen
		default:
			
			break;
		}
		//and breakdown
		int breakdownTime = sim.getCurrentTime()+sim.getMachineStage1(machineNumber).generateBreakDownTime();
		int repairTime =  sim.getMachineStage1(machineNumber).generateRepairTime();
		Event machinestage1breakdown = new Stage1BreakDown(breakdownTime,sim.getCurrentTime(), m.machineNumber, repairTime,this.getClass().getSimpleName());
		sim.addToEventQueue(machinestage1breakdown);
	}

	@Override
	protected void updateStatistics(Simulation sim) {
		
	}
}