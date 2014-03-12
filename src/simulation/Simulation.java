package simulation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;

import machines.ConveyorBelt;
import machines.MachineStageOne;
import machines.MachineStageTwo;
import misc.DVD;
import misc.Statistics;
import buffer.Buffer;
import events.Event;
import events.MachineXStage1Breakdown;
import events.MachineXStage1FinishedDVD;
import events.SimulationFinished;
import exceptions.BufferOverflowException;
import exceptions.BufferUnderflowException;

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
	
	private ArrayList<MachineStageOne> stageOneMachines;
	private ArrayList<MachineStageTwo> stageTwoMachines;
	private ArrayList<ConveyorBelt> conveyorBelts;
	//TODO:remove this in final
	public int DVDsprocessed;
	public static int hours = 1;
	public static void main(String [] args) {
		if (args.length < 3) {
			System.out.println("Not enough arguments!");
			return;
		}
		int runTime = Integer.parseInt(args[0]);
		int maxBufferSize = Integer.parseInt(args[1]);
		int batchSize = Integer.parseInt(args[2]);
		runTime =hours*60*60;
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

	public MachineStageOne getMachineStage1(int machineNumber)
	{
		return stageOneMachines.get(machineNumber-1);
	}
	
	public MachineStageTwo getMachineStage2(int machineNumber)
	{
		return stageTwoMachines.get(machineNumber-1);
	}
	
	public ConveyorBelt getConveyorBelt (int conveyorBeltNumber)
	{
		return conveyorBelts.get(conveyorBeltNumber-1);
	}
	
	public void addToEventQueue(Event e) {
		eventQueue.add(e);
	}

	private void createMachines() {
		stageOneMachines = new ArrayList<MachineStageOne>();
		stageTwoMachines = new ArrayList<MachineStageTwo>();
		conveyorBelts = new ArrayList<ConveyorBelt>();
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
			stageTwoMachines.add(new MachineStageTwo(i,layerOneBuffers.get(i-1)));
			conveyorBelts.add(new ConveyorBelt(i, layerTwoBuffers.get(i-1)));
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
		for(MachineStageOne m : stageOneMachines) 
		{
			// production
			DVD dvd = new DVD(currentTime);
			try {
				m.addDVD(dvd);
			} catch (BufferOverflowException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
		
			int machineProcTime = stageOneMachines.get(m.machineNumber-1).generateProcessingTime();
			int machineFinishedTime = machineProcTime + currentTime;
			Event machinestage1 = new MachineXStage1FinishedDVD(machineFinishedTime, m.machineNumber, machineProcTime);
			eventQueue.add(machinestage1);
			
			//and breakdown
			int breakdownTime = currentTime+stageOneMachines.get(m.machineNumber-1).generateBreakDownTime();
			int repairTime =  Math.round(stageOneMachines.get(m.machineNumber-1).generateRepairTime());
			Event machinestage1breakdown = new MachineXStage1Breakdown(breakdownTime, m.machineNumber, repairTime);
			eventQueue.add(machinestage1breakdown);
		}
		// when are we finished with the simulation
		Event simulationFinished = new SimulationFinished(runTime);
		eventQueue.add(simulationFinished);
	}
	
	private void printState()
	{
		Event[] eventList = new Event[eventQueue.size()];
		eventQueue.toArray(eventList) ;
		Arrays.sort(eventList);
		System.out.println("EventQueue: ");
		for(int i =0; i < eventQueue.size();i++) {
			System.out.println(eventList[i].getClass().getSimpleName() + " at "+  eventList[i].getTimeOfOccurence());
		}
		System.out.println();
		System.out.println("Machines Stage 1");
		for(int i =0;i<4;i++)
		{
			System.out.println("Machine " +(i+1)+" state: " + stageOneMachines.get(i).state);
		}
		System.out.println("Buffers between Stage 1 and 2");
		for(int i =0;i<2;i++)
		{
			System.out.println("Buffer " +(i+1)+" size: "+ layerOneBuffers.get(i).currentDVDCount()  + " ("+Math.round(layerTwoBuffers.get(i).currentLoad()*100)+")%");
		}
		System.out.println("Machines Stage 2");
		for(int i =0;i<2;i++)
		{
			System.out.println("Machine " +(i+1)+" state: " + stageTwoMachines.get(i).state);
		}
		System.out.println("Conveyor Belts");
		for(int i =0;i<2;i++)
		{
			System.out.println("CB " +(i+1)+" state: " + conveyorBelts.get(i).state);
		}
		System.out.println("Crates between Conveyor Belt and Stage 3");
		for(int i =0;i<2;i++)
		{
			System.out.println("Crate " +(i+1)+" size: "+ layerTwoBuffers.get(i).currentDVDCount() + " ("+Math.round(layerTwoBuffers.get(i).currentLoad()*100)+")%");
		}
		System.out.println("Crates between Stage 3 and Stage 4");
		for(int i =0;i<2;i++)
		{
			System.out.println("Crate " +(i+1)+" size: "+ layerThreeBuffers.get(i).currentDVDCount()  + " ("+Math.round(layerTwoBuffers.get(i).currentLoad()*100)+")%");
		}
	}
	
	public void run() {
		do {
			System.out.println("The current time is " + currentTime);
			//printState();
			
			Event event = eventQueue.remove();
			currentTime = event.getTimeOfOccurence();
			System.out.println("The event that will be processed is " + event.getClass().getSimpleName());
			
			event.execute(this);
		
			System.out.println();
		} while(!simulationFinished);
	}
}
