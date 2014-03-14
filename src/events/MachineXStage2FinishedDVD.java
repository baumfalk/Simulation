package events;

import machines.ConveyorBelt;
import machines.MachineStage1;
import machines.MachineStage2;
import misc.DVD;
import simulation.Simulation;
import states.StateConveyorBelt;
import states.StateStage1;
import states.StateStage2;

public class MachineXStage2FinishedDVD extends MachineXEvent {
	
	
	public MachineXStage2FinishedDVD(int t, int m, int p) {
		super(t, m);
	
		procTime = p;
	}

	private final int procTime;
	private MachineStage2 m ;

	public int getProcTime() {
		return procTime;
	}

	@Override
	public void execute(Simulation sim) {
		System.out.println("\t Looking at Stage 2, machine " + machineNumber);
		m = sim.getMachineStage2(machineNumber);
		// normal
		if(m.breakDVD()) {
			System.out.println("\t Machine " + m.machineNumber + " broke a DVD. :-(");
			m.removeDVD();
			return;
		}
		
		System.out.println("\t Didn't break the DVD!");
		ConveyorBelt cb = sim.getConveyorBelt(m.machineNumber);
		
		if(cb.state == StateConveyorBelt.Blocked)
		{
			m.state = StateStage2.Blocked;//TODO: enhance this
		} 
		switch(m.state)
		{
		case Idle:
			handleIdleState();
			break;
		case Running:
			handleRunningState(sim);
			break;
		default:
			break;
		
		}
	}
	
	private void handleIdleState()
	{
		System.out.println("\t I'm idle, doing nothing");
	}
	
	private void handleRunningState(Simulation sim)
	{
		sim.DVDsprocessed++;
	
		scheduleNewConveyorBeltEvent(sim);
		scheduleNewDVDEvent(sim);
	}
	
	private void scheduleNewConveyorBeltEvent(Simulation sim) {

		DVD dvdTemp = null;
		ConveyorBelt cb = sim.getConveyorBelt(m.machineNumber);
		dvdTemp = m.removeDVD();
		System.out.println("Removed the DVD from the machine");
		
		dvdTemp.timeOfEnteringConveyorBelt = sim.getCurrentTime();
		cb.addDVD(dvdTemp);
		Event conveyorEvent = new ConveyorBeltXFinishedDVD(sim.getCurrentTime()+cb.generateProcessingTime(), m.machineNumber);
		sim.addToEventQueue(conveyorEvent);
		if(cb.state == StateConveyorBelt.Idle) {
			System.out.println("\t Reactivated the conveyorbelt!");
			cb.state = StateConveyorBelt.Running;
		}
	}
	
	private void scheduleNewDVDEvent(Simulation sim)
	{
		// buffer to the left empty?
		if(m.leftBuffer().isEmpty())
		{
			m.state = StateStage2.Idle;
			System.out.println("\t Buffer empty, going idle");
		} else {
			DVD dvd= m.leftBuffer().removeFromBuffer();
			m.addDVD(dvd);
			//schedule new event for this machine
			int machineProcTime = m.generateProcessingTime(); 
			int machineFinishedTime = machineProcTime + sim.getCurrentTime();
			Event eventStage2FinishedDVD = new MachineXStage2FinishedDVD(machineFinishedTime, m.machineNumber, machineProcTime);
			sim.addToEventQueue(eventStage2FinishedDVD);
			
			reactivateStageOne(sim);
		}
	}
	
	private void reactivateStageOne(Simulation sim)
	{
		MachineStage1 s1m1 = sim.getMachineStage1(m.machineNumber*2-1);
		MachineStage1 s1m2 = sim.getMachineStage1(m.machineNumber*2);
		if(s1m1.state == StateStage1.Blocked)
		{
			s1m1.state = StateStage1.Running;
			Event event_s1_m1 = new MachineXStage1FinishedDVD(sim.getCurrentTime(),s1m1.machineNumber,s1m1.totalProcessingTime);
			sim.addToEventQueue(event_s1_m1);
			System.out.println("\t Reactivating machine at stage 1");
		}
		if(s1m2.state == StateStage1.Blocked)
		{
			s1m2.state = StateStage1.Running;
			Event event_s1_m2 = new MachineXStage1FinishedDVD(sim.getCurrentTime(),s1m1.machineNumber,s1m1.totalProcessingTime);
			sim.addToEventQueue(event_s1_m2);
			System.out.println("\t Reactivating machine at stage 1");
		}
	}
}
