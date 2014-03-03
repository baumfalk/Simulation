package Events;

import Misc.DVD;

public class MachineXStage1Breakdown extends Event {
	public MachineXStage1Breakdown(int t, int m, int r) {
		super(t);
		machineNumber = m;
		repairTime = r;
	
	}

	private final int machineNumber;
	private final int repairTime;
	
	public int getMachineNumber() {
		return machineNumber;
	}

	public int getRepairTime() {
		return repairTime;
	}
}
