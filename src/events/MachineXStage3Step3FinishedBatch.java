package events;

import machines.ConveyorBelt;
import machines.MachineStage3;
import machines.MachineStage4;
import misc.DVD;
import simulation.Simulation;
import states.StateConveyorBelt;
import states.StateStage3;
import states.StateStage4;

public class MachineXStage3Step3FinishedBatch extends MachineXEvent {

	private MachineStage3 m;

	public MachineXStage3Step3FinishedBatch(int t, int tos, int m,String scheduledBy) {
		super(t, tos,m, scheduledBy);
	}

	@Override
	public void execute(Simulation sim) {
		
		MachineStage4 s4m1 = sim.getMachineStage4(machineNumber);
		MachineStage4 s4m2 = sim.getMachineStage4(3-machineNumber);
		m = sim.getMachineStage3(machineNumber);
		if(s4m1.state == StateStage4.Idle) {
			addStage4Event(sim, s4m1);
		} else if(s4m2.state == StateStage4.Idle) {
			addStage4Event(sim, s4m2);
		} else {
			if (m.rightBuffer(machineNumber - 1).isEmpty()) {
				m.rightBuffer(m.machineNumber - 1).addBatchToBuffer(m.removeBatch());
				m.state = StateStage3.Idle;
			} else if (m.rightBuffer(2 - machineNumber).isEmpty()) {
				m.rightBuffer(2 - machineNumber).addBatchToBuffer(m.removeBatch());
				m.state = StateStage3.Idle;
			} else {
				m.state = StateStage3.Blocked;
				System.out.println(" \t Stage 3 machine " + machineNumber
						+ " blocked!");
			}
		}
		ConveyorBelt cb = sim.getConveyorBelt(machineNumber);
		if(cb.getState() == StateConveyorBelt.Blocked) {
			cb.setRunning(); // update the expected finished time for all dvd's
			// for all dvds whose events have already occurred
			for(DVD dvd : cb.peekBuffer()) {
				// reschedule those
				if(dvd.expectedLeavingTimeConveyorBelt <= sim.getCurrentTime()) {
					Event conveyorEvent = new ConveyorBeltXFinishedDVD(dvd.expectedLeavingTimeConveyorBelt+(sim.getCurrentTime()-cb.beginDelayTime),sim.getCurrentTime(), cb.machineNumber,this.getClass().getSimpleName());
					sim.addToEventQueue(conveyorEvent);
					System.out.println("\t Rescheduling dvd!");
				}
			}
			// update finishing time for all dvd's in the buffer
			cb.updateExpectedFinishingTime(sim.getCurrentTime()-cb.beginDelayTime);
			cb.beginDelayTime = -1;
		}
	}
	
	// directly add the buffer to stage 4.
	private void addStage4Event(Simulation sim, MachineStage4 s4m) {
		m.rightBuffer(s4m.machineNumber-1).addBatchToBuffer(m.removeBatch());
		DVD dvd = m.rightBuffer(s4m.machineNumber-1).removeFromBuffer();
		s4m.addDVD(dvd);
		s4m.state = StateStage4.Running;
		int processingTime = s4m.generateProcessingTime();
		int machineFinishedTime = sim.getCurrentTime() + processingTime; 
		Event stage4finished =  new MachineXStage4FinishedDVD(machineFinishedTime,sim.getCurrentTime(), s4m.machineNumber,this.getClass().getSimpleName());
		sim.addToEventQueue(stage4finished);
		m.state = StateStage3.Idle;
	}

	@Override
	public void updateMachines(Simulation sim) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void scheduleEvents(Simulation sim) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateStatistics(Simulation sim) {
		// TODO Auto-generated method stub
		
	}
	
}
