package events;

import exceptions.InvalidStateException;
import machines.ConveyorBelt;
import machines.MachineStage1;
import machines.MachineStage2;
import misc.DVD;
import simulation.Simulation;
import states.StateConveyorBelt;
import states.StateStage1;

public class Stage2Finished extends MachineXEvent {
	
	
	public Stage2Finished(int t, int tos, int m, int p,String scheduledBy) {
		super(t, tos,m, scheduledBy);
	
		procTime = p;
	}

	private final int procTime;
	private MachineStage2 m ;
	private ConveyorBelt cb;
	private MachineStage1 s1m1;
	private MachineStage1 s1m2;

	public int getProcTime() {
		return procTime;
	}

	@Override
	protected void scheduleEvents(Simulation sim) {
		// TODO Auto-generated method stub
		m = sim.getMachineStage2(machineNumber);
		cb = sim.getConveyorBelt(m.machineNumber);
		s1m1 = sim.getMachineStage1(m.machineNumber*2-1);
		s1m2 = sim.getMachineStage1(m.machineNumber*2);
		switch(m.getState()) {
		case Running:
			// schedule new cb event
			if(cb.getState() != StateConveyorBelt.Blocked)
			{
				Event conveyorEvent = new CBFinished(sim.getCurrentTime()+cb.generateProcessingTime(),sim.getCurrentTime(), m.machineNumber,this.getClass().getSimpleName());
				sim.addToEventQueue(conveyorEvent);
			
				// schedule new stage 2 event
				if(!m.leftBuffer().isEmpty()) {
					
					//schedule new event for this machine
					int machineProcTime = m.generateProcessingTime(); 
					int machineFinishedTime = machineProcTime + sim.getCurrentTime();
					Event eventStage2FinishedDVD = new Stage2Finished(machineFinishedTime,sim.getCurrentTime(), m.machineNumber, machineProcTime,this.getClass().getSimpleName());
					sim.addToEventQueue(eventStage2FinishedDVD);
				
					if(s1m1.getState() == StateStage1.Blocked) {
						//s1m1.setRunning();
						Event event_s1_m1 = new Stage1Finished(sim.getCurrentTime(),sim.getCurrentTime(),s1m1.machineNumber,s1m1.totalProcessingTime,this.getClass().getSimpleName());
						sim.addToEventQueue(event_s1_m1);
						System.out.println("\t Reactivating machine at stage 1");
					}
					if(s1m2.getState() == StateStage1.Blocked) {
						//s1m2.setRunning();
						Event event_s1_m2 = new Stage1Finished(sim.getCurrentTime(),sim.getCurrentTime(),s1m1.machineNumber,s1m1.totalProcessingTime,this.getClass().getSimpleName());
						sim.addToEventQueue(event_s1_m2);
						System.out.println("\t Reactivating machine at stage 1");
					}			
				}
			}
			break;
		default:
			crashOnState();
			break;
		}
	}

	@Override
	protected void updateMachines(Simulation sim) {
		switch(m.getState()) {
		case Blocked:
			break;
		case Idle:
			break;
		case Running:
			if(m.breakDVD()) {
				System.out.println("\t Machine " + m.machineNumber + " broke a DVD. :-(");
				m.removeDVD();
			}
			
			if(cb.getState() == StateConveyorBelt.Blocked)
			{
				m.setBlocked();
			} else {
				DVD dvdTemp =  m.removeDVD();
				
				dvdTemp.timeOfEnteringConveyorBelt = sim.getCurrentTime();
				dvdTemp.expectedLeavingTimeConveyorBelt = dvdTemp.timeOfEnteringConveyorBelt + cb.generateProcessingTime();
				cb.addDVD(dvdTemp);
				
				if(m.leftBuffer().isEmpty()) {
					m.setIdle();
				}
				// schedule new stage 2 event
				else {
					DVD dvd= m.leftBuffer().removeFromBuffer();
					m.addDVD(dvd);
					if(s1m1.getState() == StateStage1.Blocked) {
						s1m1.setRunning();
						System.out.println("\t Reactivating machine at stage 1");
					}
					if(s1m2.getState() == StateStage1.Blocked) {
						s1m2.setRunning();
						System.out.println("\t Reactivating machine at stage 1");
					}
				}
				
				if(cb.getState() == StateConveyorBelt.Idle) {
					cb.setRunning();
				}
			}
			break;
		default:
			crashOnState();
			break;
		}
	}

	private void crashOnState() {
		try {
			throw new InvalidStateException();
		} catch (InvalidStateException e) {
			
			e.printStackTrace();
			System.out.println("\t State " + m.getState() + " is invalid for the event " + this.getClass().getSimpleName() + "!");
			System.exit(1);
		}
	}

	@Override
	protected void updateStatistics(Simulation sim) {
		// TODO Auto-generated method stub
	}
}
