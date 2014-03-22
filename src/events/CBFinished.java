package events;

import machines.ConveyorBelt;
import machines.MachineStage2;
import machines.MachineStage3;
import simulation.Simulation;
import states.StateStage2;
import states.StateStage3;

public class CBFinished extends MachineXEvent {

	public CBFinished(int t, int tos, int c, String scheduledBy) {
		super(t,tos, c, scheduledBy);
	}
	
	private ConveyorBelt cb;
	
	@Override
	public void execute(Simulation sim) {
		// TODO Auto-generated method stub
		cb = sim.getConveyorBelt(machineNumber);
		switch (cb.getState()) {
		case Idle:
			break;
		case Running:
			handleRunningState(sim);
			
			break;
		case Blocked:
			break;
		default:
			break;
		}
	}
	
	private void handleRunningState(Simulation sim) {
		if(cb.peekDVD().expectedLeavingTimeConveyorBelt > sim.getCurrentTime()) {
			System.out.println("WAT");
			System.exit(1);
			Event conveyorEvent = new CBFinished(cb.peekDVD().expectedLeavingTimeConveyorBelt,sim.getCurrentTime(), cb.machineNumber,this.getClass().getSimpleName());
			sim.addToEventQueue(conveyorEvent);
		} else {
			if(!cb.rightBuffer().isFull()) {
				handleCrateNotFull();
			} else {
				handleCrateFull(sim);
			}
		}
	}
	
	private void handleCrateFull(Simulation sim)
	{
		MachineStage3 s3m1 = sim.getMachineStage3(machineNumber);
		MachineStage3 s3m2 = sim.getMachineStage3(3-machineNumber);

		if(s3m1.state == StateStage3.Idle)
		{
			handleStageThreeEmpty(sim,s3m1);
		} else if(s3m2.state == StateStage3.Idle) {
			handleStageThreeEmpty(sim,s3m2);
		} else {
			handleStageThreeAllFull(sim);
		}
	}

	private void handleStageThreeAllFull(Simulation sim) {
		System.out.println("\t All machines at stage 3 are busy!");
		cb.setBlocked();
		cb.beginDelayTime = sim.getCurrentTime();
	}

	private void handleStageThreeEmpty(Simulation sim, MachineStage3 s3m) {
		s3m.addBatch(cb.rightBuffer().emptyBuffer());
		s3m.state = StateStage3.Running;
		scheduleNewStage3Event(sim, s3m);
		MachineStage2 s2m = sim.getMachineStage2(machineNumber);
		if(s2m.getState() == StateStage2.Blocked)
		{
			int machineProcTimeM2 = 0;
			s2m.setRunning();
			int machineFinishedTimeM2 = sim.getCurrentTime();
			Event event_m2 = new Stage2Finished(machineFinishedTimeM2,sim.getCurrentTime(),s2m.machineNumber, machineProcTimeM2,this.getClass().getSimpleName());
			sim.addToEventQueue(event_m2);
		}
	}

	private void scheduleNewStage3Event(Simulation sim, MachineStage3 s3m) {
		int processingTimeStep1 = s3m.generateProcessingTimeStep1();
		int machineFinishedTime = sim.getCurrentTime() + processingTimeStep1;
		Event eventStage3Step1Finished = new Stage3Step1Finished(machineFinishedTime,sim.getCurrentTime(), s3m.machineNumber,this.getClass().getSimpleName());
		sim.addToEventQueue(eventStage3Step1Finished);
	}

	private void handleCrateNotFull()
	{
		cb.rightBuffer().addToBuffer(cb.removeDVD());
		
		if(cb.machineIsEmpty()) {
			cb.setIdle();
		}
	}

	@Override
	protected void scheduleEvents(Simulation sim) {
		cb = sim.getConveyorBelt(machineNumber);
		switch (cb.getState()) {
		case Idle:
			break;
		case Running:
			// this should never happen?
//			if(cb.peekDVD().expectedLeavingTimeConveyorBelt > sim.getCurrentTime()) {
//				Event conveyorEvent = new ConveyorBeltXFinishedDVD(cb.peekDVD().expectedLeavingTimeConveyorBelt,sim.getCurrentTime(), cb.machineNumber,this.getClass().getSimpleName());
//				sim.addToEventQueue(conveyorEvent);
//			} else {
			// Crate to the right not full, proceed normally
			if(!cb.rightBuffer().isFull()) {
				handleCrateNotFull();
			} else {
				handleCrateFull(sim);
			}
		//	}
			break;
		case Blocked:
			break;
		default:
			break;
		}
	}

	@Override
	protected void updateMachines(Simulation sim) {
		cb = sim.getConveyorBelt(machineNumber);
		switch (cb.getState()) {
		case Idle:
			break;
		case Running:
			// this should never happen?
//			if(cb.peekDVD().expectedLeavingTimeConveyorBelt > sim.getCurrentTime()) {
//				Event conveyorEvent = new ConveyorBeltXFinishedDVD(cb.peekDVD().expectedLeavingTimeConveyorBelt,sim.getCurrentTime(), cb.machineNumber,this.getClass().getSimpleName());
//				sim.addToEventQueue(conveyorEvent);
//			} else {
			// Crate to the right not full, proceed normally
			if(!cb.rightBuffer().isFull()) {
				cb.rightBuffer().addToBuffer(cb.removeDVD());
				
				if(cb.machineIsEmpty()) {
					cb.setIdle();
					// reschedule stage 2 if it is idle
					
				}
			} 
			// Crate to the right full, try to put the crate in stage 3
			else {
				MachineStage3 s3m1 = sim.getMachineStage3(machineNumber);
				MachineStage3 s3m2 = sim.getMachineStage3(3-machineNumber);

				if(s3m1.state == StateStage3.Idle)
				{
					handleStageThreeEmpty(sim,s3m1);
				} else if(s3m2.state == StateStage3.Idle) {
					handleStageThreeEmpty(sim,s3m2);
				} else {
					handleStageThreeAllFull(sim);
				}
			}
		//	}
			break;
		case Blocked:
			break;
		default:
			break;
		}
	}

	@Override
	protected void updateStatistics(Simulation sim) {
		// TODO Auto-generated method stub
		
	}
}
