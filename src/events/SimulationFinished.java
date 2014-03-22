package events;

import simulation.Simulation;

public class SimulationFinished extends Event{

	public SimulationFinished(int t, int tos,String scheduledBy) {
		super(t, tos,scheduledBy);
	}

	@Override
	protected void scheduleEvents(Simulation sim) {
		
	}

	@Override
	protected void updateStatistics(Simulation sim) {
		sim.simulationFinished = true;
		System.out.println(sim.DVDsprocessed/Simulation.hours);
		System.out.println(sim.statistics);
	}

	

}
