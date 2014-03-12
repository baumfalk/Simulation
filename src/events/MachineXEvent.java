package events;

public abstract class MachineXEvent extends Event {

	protected final int machineNumber;
	public MachineXEvent(int t, int m) {
		super(t);
		machineNumber = m;
	
	}

	public int getMachineNumber() {
		return machineNumber;
	}

}
