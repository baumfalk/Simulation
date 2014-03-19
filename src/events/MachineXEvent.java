package events;

import simulation.Simulation;

public abstract class MachineXEvent extends Event {

	protected final int machineNumber;
	public MachineXEvent(int t, int tos, int m, String scheduledBy) {
		super(t, tos,scheduledBy);
		machineNumber = m;
	
	}

	public int getMachineNumber() {
		return machineNumber;
	}
	
	@Override
	public void execute(Simulation sim) {
		super.execute(sim);
		updateMachines(sim);		
	}

	public abstract void updateMachines(Simulation sim);
	
}
