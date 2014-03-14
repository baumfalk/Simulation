package events;

import machines.ConveyorBelt;
import machines.MachineStage2;
import machines.MachineStage3;
import simulation.Simulation;
import states.StateConveyorBelt;
import states.StateStage2;
import states.StateStage3;

public class ConveyorBeltXFinishedDVD extends MachineXEvent {

	public ConveyorBeltXFinishedDVD(int t, int c) {
		super(t, c);
	}
	
	private ConveyorBelt cb;
	
	@Override
	//TODO: do something with delay.
	public void execute(Simulation sim) {
		// TODO Auto-generated method stub
		cb = sim.getConveyorBelt(machineNumber);
		switch (cb.state) {
		case Idle:
			break;
		case Running:
			handleRunningState(sim);
			//batch (buffer) to the right is not full
			//TODO: make this nicer
			//if
			/*if(sim.layerTwoBuffers.get(conveyorbeltNumber-1).size()!=sim.batchSize) {
				sim.layerTwoBuffers.get(cb.conveyorBeltNumber-1).add(cb);
				cb.dvdsOnBelt.pop();
			} else {
				System.out.println("\t Buffer right to Conveyor Belt "+ cb.conveyorBeltNumber +" is full! Going Idle");
				cb.state = StateConveyorBelt.Idle;
				cb.timePaused = sim.getCurrentTime();
			}*/
			break;
		case Blocked:
			break;
		default:
			break;
		}
		// buffer to the right is full
	}
	
	private void handleRunningState(Simulation sim) {
		if(!cb.rightBuffer().isFull()) {
			handleCrateNotFull();
		} else {
			
			handleCrateFull(sim);
		}
	}
	private void handleCrateFull(Simulation sim)
	{
		MachineStage3 s3m1 = sim.getMachineStage3(machineNumber);
		MachineStage3 s3m2 = sim.getMachineStage3(3-machineNumber);

		if(s3m1.state == StateStage3.Idle)
		{
			System.out.println("em");
			handleStageThreeEmpty(sim,s3m1);
		} else if(s3m2.state == StateStage3.Idle) {
			handleStageThreeEmpty(sim,s3m2);
		} else {
			handleStageThreeAllFull(sim);
		}
	}

	private void handleStageThreeAllFull(Simulation sim) {
		System.out.println("\t All machines at stage 3 are busy!");
		cb.state = StateConveyorBelt.Blocked;
	}

	private void handleStageThreeEmpty(Simulation sim, MachineStage3 s3m) {
		s3m.addBatch(cb.rightBuffer().emptyBuffer());
		s3m.state = StateStage3.Running;
		scheduleNewStage3Event(sim, s3m);
		MachineStage2 s2m = sim.getMachineStage2(machineNumber);
		if(s2m.state == StateStage2.Blocked)
		{
			int machineProcTimeM2 = 0;
			s2m.state = StateStage2.Running;
			int machineFinishedTimeM2 = sim.getCurrentTime();
			Event event_m2 = new MachineXStage2FinishedDVD(machineFinishedTimeM2,s2m.machineNumber, machineProcTimeM2);
			sim.addToEventQueue(event_m2);
		}
	}

	private void scheduleNewStage3Event(Simulation sim, MachineStage3 s3m) {
		int processingTimeStep1 = s3m.generateProcessingTimeStep1();
		int machineFinishedTime = sim.getCurrentTime() + processingTimeStep1;
		Event eventStage3Step1Finished = new MachineXStage3Step1FinishedBatch(machineFinishedTime, s3m.machineNumber);
		sim.addToEventQueue(eventStage3Step1Finished);
	}

	private void handleCrateNotFull()
	{
		cb.rightBuffer().addToBuffer(cb.removeDVD());
		
		if(cb.machineIsEmpty()) {
			cb.state = StateConveyorBelt.Idle;
		}
		
	}

	@Override
	public void scheduleEvents(Simulation sim) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateMachines(Simulation sim) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateStatistics(Simulation sim) {
		// TODO Auto-generated method stub
		
	}
}
