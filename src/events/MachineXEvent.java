package events;

import simulation.Simulation;

public abstract class MachineXEvent extends Event {

	protected final int machineNumber;
	public MachineXEvent(int t, int m) {
		super(t);
		machineNumber = m;
	
	}

	public int getMachineNumber() {
		return machineNumber;
	}
	
	public void execute(Simulation sim) {
		super.execute(sim);
		updateMachines(sim);		
	}

	public abstract void updateMachines(Simulation sim);
}
