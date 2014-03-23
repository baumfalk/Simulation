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
import buffer.DVDBuffer;
import events.CBFinished;
import events.Event;
import events.Stage1Breakdown;
import events.Stage1Finished;
import events.SimulationFinished;
import events.Stage1Repaired;
import events.Stage2Finished;
import exceptions.EventAlreadyInQueueException;
import exceptions.InvalidTimeError;

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

	private ArrayList<DVDBuffer> layerOneBuffers;
	public ArrayList<DVDBuffer> layerTwoBuffers;
	private ArrayList<DVDBuffer> layerThreeBuffers;
	
	private ArrayList<MachineStage1> stageOneMachines;
	private ArrayList<MachineStage2> stageTwoMachines;
	private ArrayList<ConveyorBelt> conveyorBelts;
	private ArrayList<MachineStage3> stageThreeMachines;
	private ArrayList<MachineStage4> stageFourMachines;
	private int[] stage1FinishedCounter;
	private int[] stage2FinishedCounter;
	private int[] stage1BreakdownCounter;
	private int[] stage1RepairedCounter;
	private int[] stage3Step1FinishedCounter;
	private int[] stage3Step2FinishedCounter;
	private int[] stage3Step3FinishedCounter;
	private int[] stage4FinishedCounter;

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
		createCounters();
		createBuffers();
		createMachines();
		createStatistics();

		setup();
	}


	private void createCounters() {
		stage1FinishedCounter = new int[4];
		stage1BreakdownCounter = new int[4];
		stage1RepairedCounter = new int[4];
		for (int i = 0; i < stage1FinishedCounter.length; i++) {
			stage1FinishedCounter[i] = 0;
			stage1BreakdownCounter[i] = 0;
			stage1RepairedCounter[i] = 0;
		}
		
		stage2FinishedCounter = new int[2];
		stage3Step1FinishedCounter = new int[2];
		stage3Step2FinishedCounter = new int[2];
		stage3Step3FinishedCounter = new int[2];
		stage4FinishedCounter = new int[2];
		for (int i = 0; i < stage2FinishedCounter.length; i++) {
			stage2FinishedCounter[i] = 0;
			stage3Step1FinishedCounter[i] = 0;
			stage3Step2FinishedCounter[i] = 0;
			stage3Step3FinishedCounter[i] = 0;
			stage4FinishedCounter[i] = 0;
		}
	}
	
	public ArrayList<String> getEventListString()
	{
		ArrayList<String> listString = new ArrayList<String>();
		Event[] eventList = new Event[eventQueue.size()];
		eventQueue.toArray(eventList) ;
		Arrays.sort(eventList);
		for(int i =0; i < eventQueue.size();i++) {		
			listString.add(eventList[i].toString());
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
	
	public void addToEventQueue(Event event) {
		/*
		 * It is not possible to add events to the past.
		 */
		if(event.getTimeOfOccurrence() < currentTime) {
			try {
				throw new InvalidTimeError();
			} catch (InvalidTimeError e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		eventQueue.add(event);
		System.out.println("Added event " + event.getClass().getSimpleName() + " to the event queue.");
		System.out.println("Added on " + event.getTimeOfScheduling() + " for time " + event.getTimeOfOccurrence());
	}

	private void createBuffers()  {
		layerOneBuffers = new ArrayList<DVDBuffer>();
		layerTwoBuffers = new ArrayList<DVDBuffer>();
		layerThreeBuffers = new ArrayList<DVDBuffer>();
		
		for(int i =0; i<2;i++) {
			layerOneBuffers.add(new DVDBuffer(maxBufferSize));
			layerTwoBuffers.add(new DVDBuffer(batchSize));
			layerThreeBuffers.add(new DVDBuffer(batchSize));
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
		
		statistics.addStatistic("Total DVDs processed", 0f, "DVDs");
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
		
			int processingTime = m.generateProcessingTime();
			m.setProcessingTime(processingTime);
			scheduleStage1FinishedEvent(m.machineNumber, processingTime, this.getClass().getSimpleName());
			//and breakdown
			int breakdownTime = m.generateBreakDownTime();
			scheduleStage1BreakdownEvent(m.machineNumber, breakdownTime, this.getClass().getSimpleName());
			
		}
		// when are we finished with the simulation
		Event simulationFinished = new SimulationFinished(runTime,currentTime,this.getClass().getSimpleName());
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
			return;
		}
		Event event = eventQueue.remove();
		currentTime = event.getTimeOfOccurrence();
		System.out.println("The current time is " + currentTime);
		System.out.println("The event that will be processed is " + event.getClass().getSimpleName());
		System.out.println("It was scheduled at " + event.getTimeOfScheduling() + " by " + event.getScheduler());
		
		if(event.getTimeOfOccurrence() < currentTime) {
			try {
				throw new InvalidTimeError();
			} catch (InvalidTimeError e) {
				e.printStackTrace();
				System.out.println("Current time:"+ currentTime + " " + " new time:" + event.getTimeOfOccurrence() + " scheduled at " + event.getTimeOfScheduling());
				System.exit(1);
			}
		}
		currentTime = event.getTimeOfOccurrence();
		
		event.execute(this);
		System.out.println("Executed the event");
		System.out.println();
	}


	public DVD generateNewDVD() {
		// TODO Auto-generated method stub
		return new DVD(currentTime);
	}


	public void scheduleStage1FinishedEvent(int machineNumber, int processingTime, String scheduledBy) {
		
		increaseEventCounter(stage1FinishedCounter,machineNumber);
		
		/*
		 * Add a new Stage1FinishedEvent to the event queue
		 */
		int schedulingTime = currentTime;
		int supposedFinishingTime = schedulingTime + processingTime;
		Event newStage1FinishedEvent = new Stage1Finished(supposedFinishingTime, schedulingTime, machineNumber, scheduledBy);
		
		addToEventQueue(newStage1FinishedEvent);
	}

	public void decreaseStage1FinishedEventCounter(int machineNumber) {
			decreaseEventCounter(stage1FinishedCounter,machineNumber);
	}


	public void scheduleStage1BreakdownEvent(int machineNumber,	int processingTime, String scheduledBy) {
		increaseEventCounter(stage1BreakdownCounter,machineNumber);
		
		/*
		 * Add a new Stage1FinishedEvent to the event queue
		 */
		int schedulingTime = currentTime;
		int supposedFinishingTime = schedulingTime + processingTime;
		Event newStage1BreakdownEvent = new Stage1Breakdown(supposedFinishingTime, schedulingTime, machineNumber, scheduledBy);
		
		addToEventQueue(newStage1BreakdownEvent);
		
	}


	public void decreaseStage1BreakdownEventCounter(int machineNumber) {
		decreaseEventCounter(stage1BreakdownCounter,machineNumber);
	}


	public void scheduleStage1RepairedEvent(int machineNumber,int processingTime, String scheduledBy) {
		increaseEventCounter(stage1RepairedCounter,machineNumber);
		
		/*
		 * Add a new Stage1Repaired to the event queue
		 */
		int schedulingTime = currentTime;
		int supposedFinishingTime = schedulingTime + processingTime;
		Event newStage1RepairedEvent = new Stage1Repaired(supposedFinishingTime, schedulingTime, machineNumber, scheduledBy);
		
		addToEventQueue(newStage1RepairedEvent);
		
	}

	public void decreaseStage1RepairedEventCounter(int machineNumber) {
		
		decreaseEventCounter(stage1RepairedCounter,machineNumber);
	}


	public void scheduleStage2FinishedEvent(int machineNumber, int processingTime, String scheduledBy) {
		increaseEventCounter(stage2FinishedCounter,machineNumber);
		
		/*
		 * Add a new Stage2Finished Event to the event queue
		 */
		int schedulingTime = currentTime;
		int supposedFinishingTime = schedulingTime + processingTime;
		Event newStage2FinishedEvent = new Stage2Finished(supposedFinishingTime, schedulingTime, machineNumber, scheduledBy);
		
		addToEventQueue(newStage2FinishedEvent);
		
	}

	public void decreaseStage2FinishedEventCounter(int machineNumber) {
		decreaseEventCounter(stage2FinishedCounter,machineNumber);
	}

	private void increaseEventCounter(int[] counter, int machineNumber) {
		if(counter[machineNumber-1]!= 0) {
			try {
				throw new EventAlreadyInQueueException();
			} catch (EventAlreadyInQueueException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		counter[machineNumber-1]++;
	}
	
	private void decreaseEventCounter(int[] counter, int machineNumber) {
		if(counter[machineNumber-1] != 1) {
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		counter[machineNumber-1]--;
	}


	public void scheduleCBFinishedEvent(int machineNumber, int processingTime, String scheduledBy) {
		int schedulingTime = currentTime;
		int supposedFinishingTime = schedulingTime + processingTime;
		Event newCBFinishedEvent = new CBFinished(supposedFinishingTime, schedulingTime, machineNumber, scheduledBy);
		
		addToEventQueue(newCBFinishedEvent);
	}


}
