package Events;

public class MachineXStage1Breakdown extends MachineXStage1Event {
	
	private final int repairTime;
	
	public MachineXStage1Breakdown(int t, int m, int r) {
		super(t,m);
		repairTime = r;
	
	}

	public int getRepairTime() {
		return repairTime;
	}
}
