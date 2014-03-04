package Events;

import Misc.DVD;

public class MachineXStage2FinishedDVD extends MachineXEvent {
	
	public MachineXStage2FinishedDVD(int t, int m, DVD d, int p) {
		super(t, m);
	
		finishedDVD = d;
		procTime = p;
	}

	private final DVD finishedDVD;
	private final int procTime;

	public DVD getFinishedDVD() {
		return finishedDVD;
	}

	public int getProcTime() {
		return procTime;
	}	

	
}
