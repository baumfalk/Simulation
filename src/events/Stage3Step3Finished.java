package events;

import machines.MachineStage3;
import machines.MachineStage4;
import misc.DVD;
import simulation.Simulation;
import states.StateStage4;
import buffer.DVDBuffer;

public class Stage3Step3Finished extends MachineXEvent {

	private MachineStage3 m;

	public Stage3Step3Finished(int t, int tos, int m,String scheduledBy) {
		super(t, tos,m, scheduledBy);
	}

	@Override
	public void execute(Simulation sim) {
		
		
	}
	
	@Override
	protected void scheduleEvents(Simulation sim) {
		switch(m.getState()) {
		case Blocked:
			break;
		case Idle:
			break;
		case Running:
			scheduleEventRunningCase(sim);
			break;
		default:
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
			updateMachineRunningCase(sim);
			break;
		default:
			break;
			
		}
	}

	private void scheduleEventRunningCase(Simulation sim) {
		MachineStage4 s4m1 = sim.getMachineStage4(machineNumber);
		MachineStage4 s4m2 = sim.getMachineStage4(3-machineNumber);
		m = sim.getMachineStage3(machineNumber);
		// if stage 4 machine is idle, reactivate it
		if(s4m1.state == StateStage4.Idle) {
			addStage4Event(sim, s4m1);
		} else if(s4m2.state == StateStage4.Idle) {
			addStage4Event(sim, s4m2);
		} 
	}

	private void updateMachineRunningCase(Simulation sim) {
		MachineStage4 s4m1 = sim.getMachineStage4(machineNumber);
		MachineStage4 s4m2 = sim.getMachineStage4(3-machineNumber);
		DVDBuffer rightBuffer1 = m.rightBuffer(machineNumber - 1);
		DVDBuffer rightBuffer2 = m.rightBuffer(2- machineNumber);
		m = sim.getMachineStage3(machineNumber);
		// if stage 4 machine is idle, reactivate it
		if(s4m1.state == StateStage4.Idle) {
			doIfMachineStage4IsIdle(s4m1);
		} else if(s4m2.state == StateStage4.Idle) {
			doIfMachineStage4IsIdle(s4m2);
		} else {
			doIfMachine4IsNotIdle(rightBuffer1, rightBuffer2);
		}
	}

	private void doIfMachine4IsNotIdle(DVDBuffer rightBuffer1,
			DVDBuffer rightBuffer2) {
		if (rightBuffer1.isEmpty()) {
			doIfBufferEmpty(rightBuffer1);
		} else if (rightBuffer2.isEmpty()) {
			doIfBufferEmpty(rightBuffer2);
		} else {
			m.setBlocked();
			System.out.println(" \t Stage 3 machine " + machineNumber
					+ " blocked!");
		}
	}

	private void doIfBufferEmpty(DVDBuffer rightBuffer) {
		rightBuffer.addBatchToBuffer(m.removeBatch());
		m.setIdle();
	}

	private void doIfMachineStage4IsIdle(MachineStage4 s4m) {
		m.rightBuffer(s4m.machineNumber-1).addBatchToBuffer(m.removeBatch());
		DVD dvd = m.rightBuffer(s4m.machineNumber-1).removeFromBuffer();
		s4m.addDVD(dvd);
		s4m.state = StateStage4.Running;
						
		m.setIdle();
	}

	// directly add the buffer to stage 4.
	private void addStage4Event(Simulation sim, MachineStage4 s4m) {
		int delay = 0;
		if(s4m.dvdLeft() == 0) 
		{
			delay = s4m.generateCartridgeRenewalTime();
			s4m.generateCartridgeRenewal();
			System.out.println("CARTRIDGE RENEWAL");
		} 
		int processingTime = m.generateProcessingTime()+delay;
		int machineFinishedTime = sim.getCurrentTime() + processingTime ; 
		Event stage4finished =  new Stage4Finished(machineFinishedTime,sim.getCurrentTime(), machineNumber,this.getClass().getSimpleName());
		sim.addToEventQueue(stage4finished);
	}

	@Override
	protected void updateStatistics(Simulation sim) {
		// TODO Auto-generated method stub
		
	}
	
}
