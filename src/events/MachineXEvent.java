package events;

public abstract class MachineXEvent extends Event {

	protected final int machineNumber;
	public MachineXEvent(int t, int tos, int m, String scheduledBy) {
		super(t, tos,scheduledBy);
		machineNumber = m;
	
	}

	public int getMachineNumber() {
		return machineNumber;
	}
	
	public String toString() {
		String s= super.toString();
		s = " Machine: " + machineNumber + " " + s;
		return s;
	}
	
	public String scheduledBy() {
		return this.getClass().getSimpleName() + " machine " + machineNumber;
	}
}
