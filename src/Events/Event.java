package Events;

public abstract class Event implements Comparable<Event> {
	protected final int timeOfOccurence;

	
	public Event(int t)
	{
		this.timeOfOccurence = t;
	}
	public int getTimeOfOccurence() {
		return timeOfOccurence;
	}

	
	@Override
	public int compareTo(Event event) {
		int output = 0;
		if(this.getTimeOfOccurence() < event.getTimeOfOccurence())
			output = -1;
		else if(this.getTimeOfOccurence() == event.getTimeOfOccurence())
			output = 0;
		else output = 1;
		return output;
	}
}
