package events;

import exceptions.BufferOverflowException;
import exceptions.BufferUnderflowException;
import machines.ConveyorBelt;
import simulation.Simulation;
import states.StateConveyorBelt;

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
			handleRunningState();
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
	
	private void handleRunningState() {
		if(!cb.rightBuffer().isFull()) {
			handleCrateNotFull();
		} else {
			
			handleCrateFull();
		}
	}
	private void handleCrateFull()
	{
		cb.rightBuffer().emptyBuffer();
		//cb.state = StateConveyorBelt.Blocked;
	}
	private void handleCrateNotFull()
	{
		try {
			cb.rightBuffer().addToBuffer(cb.removeDVD());
			
		} catch (BufferOverflowException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (BufferUnderflowException e) {
			e.printStackTrace();
		}
		
		if(cb.machineIsEmpty()) {
			cb.state = StateConveyorBelt.Idle;
		}
	}
}
