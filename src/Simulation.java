import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;

import Events.Event;
import Events.MachineXStage1Breakdown;
import Events.MachineXStage1FinishedDVD;
import Events.MachineXStage1Repaired;
import Events.SimulationFinished;
import Machines.Machine;
import Machines.MachineStageOne;
import Machines.MachineStageTwo;
import Misc.DVD;
import Misc.Statistics;
import Stages.StateStageOne;

public class Simulation {
	
	private int currentTime;
	
	private int runTime;
	private int maxBufferSize; // the same for every buffer
	private int batchSize;
	
	private boolean simulationFinished;
	private PriorityQueue<Event> eventQueue;
	private Statistics statistics;

	private ArrayList<LinkedList<DVD>> layerOneBuffers;
	private ArrayList<LinkedList<DVD>> layerTwoBuffers;
	private ArrayList<LinkedList<DVD>> layerThreeBuffers;
	
	private ArrayList<MachineStageOne> stageOneMachines;
	private ArrayList<MachineStageTwo> stageTwoMachines;
	
	public static void main(String [] args) {
		if (args.length < 3) {
			System.out.println("Not enough arguments!");
			return;
		}
		int runTime = Integer.parseInt(args[0]);
		int maxBufferSize = Integer.parseInt(args[1]);
		int batchSize = Integer.parseInt(args[2]);
		runTime = 500;
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
		simulationFinished = false;
		eventQueue = new PriorityQueue<Event>();
		
		
		createBuffers();
		createMachines();
		createStatistics();
		
		
		setup();
	}
	
	private void createBuffers()  {
		layerOneBuffers = new ArrayList<LinkedList<DVD>>();
		layerTwoBuffers = new ArrayList<LinkedList<DVD>>();
		layerThreeBuffers = new ArrayList<LinkedList<DVD>>();
		
		for(int i =0; i<2;i++) {
			layerOneBuffers.add(new LinkedList<DVD>());
			layerTwoBuffers.add(new LinkedList<DVD>());
			layerThreeBuffers.add(new LinkedList<DVD>());
		}
	}
	
	private void createMachines() {
		stageOneMachines = new ArrayList<MachineStageOne>();
		stageTwoMachines = new ArrayList<MachineStageTwo>();
		for (int i = 1; i <= 4;i++) 
		{
			// first two connected to buffer one,
			if(i <= 2) 
				stageOneMachines.add(new MachineStageOne(i,layerOneBuffers.get(0)));
			// second two connected to buffer two
			else 
				stageOneMachines.add(new MachineStageOne(i,layerOneBuffers.get(1)));
		}
		
		for (int i = 1; i <= 2;i++) 
		{
			stageTwoMachines.add(new MachineStageTwo(i,layerOneBuffers.get(i-1),layerTwoBuffers.get(i-1)));
		}
	}
	
	private void createStatistics() {
		statistics = new Statistics();
		for (int i = 1; i <= 4;i++) {
			statistics.addStatistic("Average processing time S1M"+i, 0f,"seconds/DVD");
		}
		for (int i = 1; i <= 2;i++) {
			statistics.addStatistic("Average processing time S2M"+i, 0f,"seconds/DVD");
		}
		statistics.addStatistic("Throughput time per DVD",0.0f,"seconds");
		statistics.addStatistic("Output per hour",0.0f,"DVD/hour");
	}
	
	private void setup()
	{
		// setup the first stage
		for(Machine m : stageOneMachines) 
		{
			// production
			DVD dvd = new DVD(currentTime);
			int machineProcTime = Math.round(stageOneMachines.get(m.machineNumber-1).generateProcessingTime());
			int machineFinishedTime = machineProcTime + currentTime;
			Event machinestage1 = new MachineXStage1FinishedDVD
					(machineFinishedTime, m.machineNumber,dvd, machineProcTime);
			eventQueue.add(machinestage1);
			
			//and breakdown
			int breakdownTime = currentTime+Math.round(stageOneMachines.get(m.machineNumber-1).generateBreakDownTime());
			int repairTime =  Math.round(stageOneMachines.get(m.machineNumber-1).generateRepairTime());
			Event machinestage1breakdown = new MachineXStage1Breakdown(breakdownTime, m.machineNumber, repairTime);
			eventQueue.add(machinestage1breakdown);
		}
		// when are we finished with the simulation
		Event simulationFinished = new SimulationFinished(runTime);
		eventQueue.add(simulationFinished);
	}
	
	public void run() {
		do {
			Event event = eventQueue.remove();
			
			currentTime = event.getTimeOfOccurence();
			System.out.println("The new time is " + currentTime);
			System.out.println("The event that will be processed is " + event.getClass().getSimpleName());
			
			if(event instanceof MachineXStage1FinishedDVD) {
				int machineNumber = ((MachineXStage1FinishedDVD) event).getMachineNumber();
				System.out.println("\t The event is for machine " + machineNumber);
				process_MachineXStage1FinishedDVD_event((MachineXStage1FinishedDVD) event);
			} 
			else if(event instanceof MachineXStage1Breakdown) {
				int machineNumber = ((MachineXStage1Breakdown) event).getMachineNumber();
				System.out.println("\t ALERT: BREAKDOWN FOR MACHINE " + machineNumber+"!");
				process_MachineXStage1Breakdown_event((MachineXStage1Breakdown) event);
			} 
			else if(event instanceof MachineXStage1Repaired) {
				int machineNumber = ((MachineXStage1Repaired) event).getMachineNumber();
				System.out.println("\t Machine " + machineNumber+" repaired!!");
				process_MachineXStage1Repaired_event((MachineXStage1Repaired) event);
			}
			else if (event instanceof SimulationFinished) {
				System.out.println("\t Finishing up the simulation at time " + currentTime);
				process_SimulationFinished_event(event);
			}
			
		} while(!simulationFinished);
	}

	private void process_MachineXStage1Repaired_event(
			MachineXStage1Repaired event) {
		MachineStageOne m = stageOneMachines.get(event.getMachineNumber()-1);
		m.state = StateStageOne.Normal;
		Event dvdFinishedEvent = new MachineXStage1FinishedDVD(m.getProcessingTimeLeft()+currentTime, m.machineNumber, m.getDvdBeingProcessed(), m.getTotalProcessingTime());
		eventQueue.add(dvdFinishedEvent);
	}

	private void process_MachineXStage1Breakdown_event(
			MachineXStage1Breakdown event) {
			stageOneMachines.get(event.getMachineNumber()-1).state = StateStageOne.Broken;
			stageOneMachines.get(event.getMachineNumber()-1).setLastBreakDownTime(event.getTimeOfOccurence());
			Event repairEvent = new MachineXStage1Repaired(currentTime+event.getRepairTime(),event.getMachineNumber());
			eventQueue.add(repairEvent);
	}

	private void process_MachineXStage1FinishedDVD_event(MachineXStage1FinishedDVD event) {
		switch(stageOneMachines.get(event.getMachineNumber()-1).state)
		{
		case Broken:
			int timeSupposedlyFinished = event.getTimeOfOccurence();
			int timeCrashed = stageOneMachines.get(event.getMachineNumber()-1).getLastBreakDownTime();
			int processingTimeLeft = timeSupposedlyFinished-timeCrashed;
			System.out.println("\t Machine broken, DVD stuck! Time still needed in machine: " + processingTimeLeft);
			// machine is broken, DVD is stuck in machine!
			stageOneMachines.get(event.getMachineNumber()-1).setProcessingTimeLeft(processingTimeLeft);
			stageOneMachines.get(event.getMachineNumber()-1).setTotalProcessingTime(event.getProcTime());
			stageOneMachines.get(event.getMachineNumber()-1).setDvdBeingProcessed(event.getFinishedDVD());
			break;
		case Idle:
			// cannot happen
			break;
		case Normal:
			statistics.updateAverage("Average processing time S1M"+  event.getMachineNumber(),
					event.getProcTime());
			MachineStageOne m = stageOneMachines.get(event.getMachineNumber()-1);
			DVD dvd = new DVD(currentTime);
			int machineProcTime = Math.round(stageOneMachines.get(m.machineNumber-1).generateProcessingTime());
			int machineFinishedTime = machineProcTime + currentTime;
			
			if(m.rightBuffer.size() == maxBufferSize)
			{
				m.state = StateStageOne.Idle;
				//TODO: statistics for idle time
				System.out.println("\t Buffer next to machine " + m.machineNumber +" is full!");
			} else {
				System.out.println(stageTwoMachines.get((m.machineNumber <=2) ? 0 : 1).leftBuffer.size());
				m.rightBuffer.add(dvd);
				System.out.println(stageTwoMachines.get((m.machineNumber <=2) ? 0 : 1).leftBuffer.size());
			}
			
			Event machinestage1 = new MachineXStage1FinishedDVD(machineFinishedTime, m.machineNumber,dvd, machineProcTime);
			eventQueue.add(machinestage1);
			System.out.println("\t DVD sucesfully processed in machine " +event.getMachineNumber());
			break;
		default:
			break;
		}
	}

	private void process_SimulationFinished_event(Event event) {
		// print statistics
		simulationFinished = true;
		System.out.println(statistics);
	}
}
