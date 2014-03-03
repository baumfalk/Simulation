package Events;

public class MachineXStage1Event extends Event {

	protected final int machineNumber;
	public MachineXStage1Event(int t, int m) {
		super(t);
		machineNumber = m;
	
	}

	public int getMachineNumber() {
		return machineNumber;
	}

}
