package Events;

import Misc.DVD;

public class ConveyorBeltXFinishedDVD extends Event {

	public ConveyorBeltXFinishedDVD(int t, int c, DVD dvd) {
		super(t);
		this.conveyorbeltNumber = c;
		this.dvd = dvd;
	}
	
	public int conveyorbeltNumber;
	public final DVD dvd;
}
