package Events;

import Misc.DVD;

public class MachineXStage1FinishedDVD extends Event {
	public MachineXStage1FinishedDVD(int t, int m, DVD d, int p) {
		super(t);
		machineNumber = m;
		finishedDVD = d;
		procTime = p;
	}

	private final int machineNumber;
	private final DVD finishedDVD;
	private final int procTime;
	
	public int getMachineNumber() {
		return machineNumber;
	}

	public DVD getFinishedDVD() {
		return finishedDVD;
	}

	public int getProcTime() {
		return procTime;
	}	
}
