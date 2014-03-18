package simulation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.PriorityQueue;

import machines.ConveyorBelt;
import machines.MachineStage1;
import machines.MachineStage2;
import machines.MachineStage3;
import machines.MachineStage4;
import misc.DVD;
import misc.Statistics;
import buffer.Buffer;
import events.Event;
import events.MachineXStage1Breakdown;
import events.MachineXStage1FinishedDVD;
import events.SimulationFinished;

public class Simulation {
	
	private int currentTime;
	
	public int getCurrentTime() {
		return currentTime;
	}

	private int runTime;
	public final int maxBufferSize; // the same for every buffer
	public final int batchSize;
	
	public boolean simulationFinished;
	private PriorityQueue<Event> eventQueue;
	public Statistics statistics;

	private ArrayList<Buffer> layerOneBuffers;
	public ArrayList<Buffer> layerTwoBuffers;
	private ArrayList<Buffer> layerThreeBuffers;
	
	private ArrayList<MachineStage1> stageOneMachines;
	private ArrayList<MachineStage2> stageTwoMachines;
	private ArrayList<ConveyorBelt> conveyorBelts;
	private ArrayList<MachineStage3> stageThreeMachines;
	private ArrayList<MachineStage4> stageFourMachines;

	
	
	//TODO:remove this in final
	public int DVDsprocessed;

	public static int hours = 24*60*60;
	
	public static void main(String [] args) {
		if (args.length < 3) {
			System.out.println("Not enough arguments!");
			return;
		}
		int runTime = Integer.parseInt(args[0]);
		int maxBufferSize = Integer.parseInt(args[1]);
		int batchSize = Integer.parseInt(args[2]);
		runTime =60*60*24*30*6;
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
		DVDsprocessed=0;
		createBuffers();
		createMachines();
		createStatistics();
		
		setup();
	}
	
	public ArrayList<String> getEventListString()
	{
		ArrayList<String> listString = new ArrayList<String>();
		Event[] eventList = new Event[eventQueue.size()];
		eventQueue.toArray(eventList) ;
		Arrays.sort(eventList);
		for(int i =0; i < eventQueue.size();i++) {		
			listString.add(eventList[i].getClass().getSimpleName() + " at "+  eventList[i].getTimeOfOccurence());
		}
		return listString;
	}


	public MachineStage1 getMachineStage1(int machineNumber)
	{
		return stageOneMachines.get(machineNumber-1);
	}
	
	public MachineStage2 getMachineStage2(int machineNumber)
	{
		return stageTwoMachines.get(machineNumber-1);
	}
	
	public MachineStage3 getMachineStage3(int machineNumber) {
		// TODO Auto-generated method stub
		return stageThreeMachines.get(machineNumber-1);
	}
	
	public MachineStage4 getMachineStage4(int machineNumber) {
		// TODO Auto-generated method stub
		return stageFourMachines.get(machineNumber-1);
	}


	public ConveyorBelt getConveyorBelt (int conveyorBeltNumber)
	{
		return conveyorBelts.get(conveyorBeltNumber-1);
	}
	
	public void addToEventQueue(Event e) {
		eventQueue.add(e);
	}

	private void createBuffers()  {
		layerOneBuffers = new ArrayList<Buffer>();
		layerTwoBuffers = new ArrayList<Buffer>();
		layerThreeBuffers = new ArrayList<Buffer>();
		
		for(int i =0; i<2;i++) {
			layerOneBuffers.add(new Buffer(maxBufferSize));
			layerTwoBuffers.add(new Buffer(batchSize));
			layerThreeBuffers.add(new Buffer(batchSize));
		}
	}


	private void createMachines() {
		stageOneMachines = new ArrayList<MachineStage1>();
		stageTwoMachines = new ArrayList<MachineStage2>();
		stageThreeMachines = new ArrayList<MachineStage3>();
		stageFourMachines = new ArrayList<MachineStage4>();
		conveyorBelts = new ArrayList<ConveyorBelt>();
		for (int i = 1; i <= 4;i++) 
		{
			// first two connected to buffer one,
			if(i <= 2) 
				stageOneMachines.add(new MachineStage1(i,layerOneBuffers.get(0)));
			// second two connected to buffer two
			else 
				stageOneMachines.add(new MachineStage1(i,layerOneBuffers.get(1)));
		}
		
		for (int i = 1; i <= 2;i++) 
		{
			stageTwoMachines.add(new MachineStage2(i,layerOneBuffers.get(i-1)));
			conveyorBelts.add(new ConveyorBelt(i, layerTwoBuffers.get(i-1)));
			// connected to all buffers
			stageThreeMachines.add(new MachineStage3(i, layerTwoBuffers, layerThreeBuffers, batchSize));
			stageFourMachines.add(new MachineStage4(i,layerThreeBuffers.get(i-1)));
		}
	}
	
	private void createStatistics() {
		statistics = new Statistics();
		
		statistics.addStatistic("Throughput time per DVD",0.0f,"seconds");
		statistics.addStatistic("Output per hour",0.0f,"DVD/hour");
	}
	
	private void setup()
	{
		// setup the first stage
		for(MachineStage1 m : stageOneMachines) 
		{
			// production
			DVD dvd = new DVD(currentTime);
			m.addDVD(dvd);
		
			int machineProcTime = stageOneMachines.get(m.machineNumber-1).generateProcessingTime();
			int machineFinishedTime = machineProcTime + currentTime;
			Event machinestage1 = new MachineXStage1FinishedDVD(machineFinishedTime, currentTime, m.machineNumber, machineProcTime);
			eventQueue.add(machinestage1);
			
			//and breakdown
			int breakdownTime = currentTime+stageOneMachines.get(m.machineNumber-1).generateBreakDownTime();
			int repairTime =  Math.round(stageOneMachines.get(m.machineNumber-1).generateRepairTime());
			Event machinestage1breakdown = new MachineXStage1Breakdown(breakdownTime, currentTime, m.machineNumber, repairTime);
			eventQueue.add(machinestage1breakdown);
		}
		// when are we finished with the simulation
		Event simulationFinished = new SimulationFinished(runTime,currentTime);
		eventQueue.add(simulationFinished);
	}
	
	public void run() {
		do {
			nextStep();
		} while(!simulationFinished);
	}

	public void nextStep()
	{
		if(simulationFinished) {
			System.out.println("Simulation is finished!");
			return;
		}
		System.out.println("The current time is " + currentTime);
		
		Event event = eventQueue.remove();
		currentTime = event.getTimeOfOccurence();
		System.out.println("The event that will be processed is " + event.getClass().getSimpleName());
		System.out.println("It was scheduled at " + event.getTimeOfScheduling());
		
		event.execute(this);
	
		System.out.println();
	}
}
