package events;

import exceptions.BufferOverflowException;
import exceptions.BufferUnderflowException;
import machines.ConveyorBelt;
import machines.MachineStageOne;
import machines.MachineStageTwo;
import misc.DVD;
import simulation.Simulation;
import states.StateConveyorBelt;
import states.StateStageOne;
import states.StateStageTwo;

public class MachineXStage2FinishedDVD extends MachineXEvent {
	
	
	public MachineXStage2FinishedDVD(int t, int m, int p) {
		super(t, m);
	
		procTime = p;
	}

	private final int procTime;
	private MachineStageTwo m ;

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
			try {
				m.removeDVD();
			} catch (BufferUnderflowException e) {
				e.printStackTrace();
				System.exit(1);
			}
			return;
		}
		
		System.out.println("\t Didn't break the DVD!");
		ConveyorBelt cb = sim.getConveyorBelt(m.machineNumber);
		
		if(cb.state == StateConveyorBelt.Blocked)
		{
			m.state = StateStageTwo.Blocked;//TODO: enhance this
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
		try {
			dvdTemp = m.removeDVD();
		} catch (BufferUnderflowException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Removed the DVD from the machine");
		dvdTemp.timeOfEnteringConveyorBelt = sim.getCurrentTime();
		try {
			cb.addDVD(dvdTemp);
		} catch (BufferOverflowException e) {
			e.printStackTrace();
			System.exit(1);
		}
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
			m.state = StateStageTwo.Idle;
			System.out.println("\t Buffer empty, going idle");
		} else {
			try {
				DVD dvd= m.leftBuffer().removeFromBuffer();
				m.addDVD(dvd);
			} catch (BufferUnderflowException e) {
				e.printStackTrace();
				System.exit(1);
			} catch (BufferOverflowException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		MachineStageOne s1m1 = sim.getMachineStage1(m.machineNumber*2-1);
		MachineStageOne s1m2 = sim.getMachineStage1(m.machineNumber*2);
		if(s1m1.state == StateStageOne.Blocked)
		{
			s1m1.state = StateStageOne.Running;
			Event event_s1_m1 = new MachineXStage1FinishedDVD(sim.getCurrentTime(),s1m1.machineNumber,s1m1.totalProcessingTime);
			sim.addToEventQueue(event_s1_m1);
			System.out.println("\t Reactivating machine at stage 1");
		}
		if(s1m2.state == StateStageOne.Blocked)
		{
			s1m2.state = StateStageOne.Running;
			Event event_s1_m2 = new MachineXStage1FinishedDVD(sim.getCurrentTime(),s1m1.machineNumber,s1m1.totalProcessingTime);
			sim.addToEventQueue(event_s1_m2);
			System.out.println("\t Reactivating machine at stage 1");
		}
	}
}
