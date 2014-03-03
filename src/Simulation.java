import java.util.ArrayList;
import java.util.PriorityQueue;

import Events.Event;
import Events.MachineXStage1Breakdown;
import Events.MachineXStage1FinishedDVD;
import Events.SimulationFinished;
import Machines.Machine;
import Machines.MachineStageOne;
import Misc.DVD;
import Misc.Statistics;
import Stages.StateStageOne;

public class Simulation {
	
	private int currentTime;
	
	private int runTime;
	private int maxBufferSize;
	private int batchSize;
	private PriorityQueue<Event> eventQueue;

	private ArrayList<MachineStageOne> stageOneMachines;

	private Statistics statistics;
	
	public static void main(String [] args) {
		if (args.length < 3) {
			System.out.println("Not enough arguments!");
			return;
		}
		int runTime = Integer.parseInt(args[0]);
		int maxBufferSize = Integer.parseInt(args[1]);
		int batchSize = Integer.parseInt(args[2]);
		System.out.println("Starting a simulation with");
		System.out.println("\t running time: " + runTime);
		System.out.println("\t maximum buffer size: " + maxBufferSize);
		System.out.println("\t batch size: " + batchSize);
		new Simulation(runTime, maxBufferSize, batchSize).run();
	}
	
	
	public Simulation(int runTime, int maxBufferSize, int batchSize) 
	{
		this.currentTime = 0;
		this.runTime = runTime;
		this.maxBufferSize = maxBufferSize;
		this.batchSize = batchSize;
		eventQueue = new PriorityQueue<Event>();
		stageOneMachines = new ArrayList<MachineStageOne>();
		
		statistics = new Statistics();
		for (int i = 1; i <= 4;i++) 
		{
			stageOneMachines.add(new MachineStageOne(i));
			statistics.addStatistic("Average processing time S1M"+i, 0f,"seconds/DVD");
		}
		statistics.addStatistic("Throughput time per DVD",0.0f,"seconds");
		statistics.addStatistic("Output per hour",0.0f,"DVD/hour");
		setup();
	}
	
	private void setup()
	{
		for(Machine m : stageOneMachines) 
		{
			DVD dvd = new DVD(currentTime);
			int machineProcTime = Math.round(stageOneMachines.get(m.machineNumber-1).generateProcessingTime());
			int machineFinishedTime = machineProcTime + currentTime;
			Event machinestage1 = new MachineXStage1FinishedDVD
					(machineFinishedTime, m.machineNumber,dvd, machineProcTime);
			eventQueue.add(machinestage1);
			
			int breakdownTime = currentTime+Math.round(stageOneMachines.get(m.machineNumber-1).generateBreakDownTime());
			int repairTime =  Math.round(stageOneMachines.get(m.machineNumber-1).generateRepairTime());
			Event machinestage1breakdown = new MachineXStage1Breakdown(breakdownTime, m.machineNumber, repairTime);
			eventQueue.add(machinestage1breakdown);
		}
		
		Event simulationFinished = new SimulationFinished(runTime);
		eventQueue.add(simulationFinished);
	}
	
	public void run() {
		boolean finished = false;
		do {
			Event event = eventQueue.remove();
			
			currentTime = event.getTimeOfOccurence();
			System.out.println("The new time is " + currentTime);
			System.out.println("The event that will be processed is " + event.getClass().getSimpleName());
			
			if(event instanceof MachineXStage1FinishedDVD) {
				int machineNumber = ((MachineXStage1FinishedDVD) event).getMachineNumber();
				System.out.println("\tThe event is for machine " + machineNumber);
				process_MachineXStage1FinishedDVD_event((MachineXStage1FinishedDVD) event);
			} 
			else if(event instanceof MachineXStage1Breakdown)
			{
				int machineNumber = ((MachineXStage1Breakdown) event).getMachineNumber();
				System.out.println("\t ALERT: BREAKDOWN FOR MACHINE " + machineNumber+"!");
				process_MachineXStage1Breakdown_event((MachineXStage1Breakdown) event);
			}
			else if (event instanceof SimulationFinished) {
				finished = true;
				System.out.println("\t Finishing up the simulation at time " + currentTime);
				process_SimulationFinished_event(event);
			}
			
		} while(!finished);
	}

	private void process_MachineXStage1Breakdown_event(
			MachineXStage1Breakdown event) {
		stageOneMachines.get(event.getMachineNumber()-1).state = StateStageOne.Broken;
		stageOneMachines.get(event.getMachineNumber()-1).setLastBreakDownTime(event.getTimeOfOccurence());
		
	}


	private void process_MachineXStage1FinishedDVD_event(MachineXStage1FinishedDVD event) {
		switch(stageOneMachines.get(event.getMachineNumber()-1).state)
		{
		case Broken:
			int processingTimeLeft = event.getTimeOfOccurence()-stageOneMachines.get(event.getMachineNumber()-1).getLastBreakDownTime();
			break;
		case Idle:
			break;
		case Normal:
			statistics.updateAverage("Average processing time S1M"+  event.getMachineNumber(),
					event.getProcTime());
			event.getFinishedDVD().setTimeOfLeavingPipeLine(currentTime);
			MachineStageOne m = stageOneMachines.get(event.getMachineNumber()-1);
			DVD dvd = new DVD(currentTime);
			int machineProcTime = Math.round(stageOneMachines.get(m.machineNumber-1).generateProcessingTime());
			int machineFinishedTime = machineProcTime + currentTime;
			Event machinestage1 = new MachineXStage1FinishedDVD
					(machineFinishedTime, m.machineNumber,dvd, machineProcTime);
			eventQueue.add(machinestage1);
			// TODO: move this to relevant timeslot
			statistics.updateAverage("Throughput time per DVD", event.getFinishedDVD().throughputTime());
			break;
		default:
			break;
		
		}
	}


	private void process_SimulationFinished_event(Event event) {
		// print statistics
		System.out.println(statistics);
	}
}
