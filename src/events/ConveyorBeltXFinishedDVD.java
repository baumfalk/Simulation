package events;

import machines.ConveyorBelt;
import simulation.Simulation;

public class ConveyorBeltXFinishedDVD extends Event {

	public ConveyorBeltXFinishedDVD(int t, int c) {
		super(t);
		this.conveyorbeltNumber = c;
	}
	
	public int conveyorbeltNumber;
	
	@Override
	public void execute(Simulation sim) {
		// TODO Auto-generated method stub
		ConveyorBelt cb = sim.getConveyorBelt(conveyorbeltNumber);
		switch (cb.state) {
		case Idle:
			break;
		case Running:
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

}
