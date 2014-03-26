package events;

import simulation.Simulation;

public class SimulationFinished extends Event{

	public SimulationFinished(int t, int tos,String scheduledBy) {
		super(t, tos,scheduledBy);
	}

	@Override
	public void execute(Simulation sim) {
		sim.simulationFinished = true;
		System.out.println(sim.largestGapInTime);
		System.out.println(sim.statistics);
	}

	

}
