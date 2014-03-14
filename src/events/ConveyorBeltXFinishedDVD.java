package events;

import machines.ConveyorBelt;
import machines.MachineStage3;
import simulation.Simulation;
import states.StateConveyorBelt;
import states.StateStage3;

public class ConveyorBeltXFinishedDVD extends Event {

	public ConveyorBeltXFinishedDVD(int t, int c) {
		super(t);
		this.conveyorbeltNumber = c;
	}
	
	public int conveyorbeltNumber;
	private ConveyorBelt cb;
	
	@Override
	public void execute(Simulation sim) {
		// TODO Auto-generated method stub
		cb = sim.getConveyorBelt(conveyorbeltNumber);
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
		MachineStage3 s3m1 = sim.getMachineStage3(conveyorbeltNumber);
		MachineStage3 s3m2 = sim.getMachineStage3(3-conveyorbeltNumber);

		if(s3m1.state == StateStage3.Idle)
		{
			System.out.println("em");
			handleStageThreeEmpty(sim,s3m1);
		} else if(s3m2.state == StateStage3.Idle) {
			handleStageThreeEmpty(sim,s3m2);
		} else {
			handleStageThreeAllFull(sim);
		}
		
		//cb.state = StateConveyorBelt.Blocked;
	}
	private void handleStageThreeAllFull(Simulation sim) {
		System.out.println("\t All machines at stage 3 are busy!");
		cb.state = StateConveyorBelt.Blocked;
	}

	private void handleStageThreeEmpty(Simulation sim, MachineStage3 s3m) {
		s3m.addBatch(cb.rightBuffer().emptyBuffer());
		s3m.state = StateStage3.Running;
		scheduleNewStage3Event(sim, s3m);
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
}
