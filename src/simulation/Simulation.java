package simulation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import events.SimulationFinished;
import events.Stage1Breakdown;
import events.Stage1Finished;
import events.Stage1Repaired;
import events.Stage2Finished;
import events.Stage3Step1Finished;
import events.Stage3Step2Finished;
import events.Stage3Step3Finished;
import events.Stage4Finished;
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
	private int[] stage1BreakdownCounter;
	private int[] stage1RepairedCounter;
	private int[] stage2FinishedCounter;
	private ArrayList<HashMap<Integer,Integer>> conveyorBeltFinishedCounter; // one 1 per DVD at any given time;
	private int[] stage3Step1FinishedCounter;
	private int[] stage3Step2FinishedCounter;
	private int[] stage3Step3FinishedCounter;
	private int[] stage4FinishedCounter;

	public static int DVDCount = 0;
	
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
		conveyorBeltFinishedCounter= new ArrayList<HashMap<Integer,Integer>>();
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
			conveyorBeltFinishedCounter.add(new HashMap<Integer,Integer>());
		}
	}
	
	public ArrayList<String> getEventListString()
	{
		ArrayList<String> listString = new ArrayList<String>();
		Event[] eventList = new Event[eventQueue.size()];
		eventQueue.toArray(eventList) ;
		Arrays.sort(eventList);
		for(int i =0; i < eventQueue.size();i++) {
			if(eventList[i] instanceof CBFinished)
				listString.add(((CBFinished) eventList[i]).toString(this));
			else
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
			DVD dvd = generateNewDVD();
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
		statistics.addToStatistic("DVDs created", 1);
		return new DVD(++DVDCount, currentTime);
	}


	public void scheduleStage1FinishedEvent(int machineNumber, int processingTime, String scheduledBy) {
		increaseEventCounter(stage1FinishedCounter,machineNumber);
		sanityCheck(!getMachineStage1(machineNumber).machineIsEmpty());
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
		sanityCheck(!getMachineStage2(machineNumber).machineIsEmpty());
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

	public void scheduleStage3Step1FinishedEvent(int machineNumber, int processingTime, String scheduledBy) {
		increaseEventCounter(stage3Step1FinishedCounter,machineNumber);
		sanityCheck(!getMachineStage3(machineNumber).machineIsEmpty());
		/*
		 * Add a new Stage3Step1Finished Event to the event queue
		 */
		int schedulingTime = currentTime;
		int supposedFinishingTime = schedulingTime + processingTime;
		Event newStage3Step1FinishedEvent = new Stage3Step1Finished(supposedFinishingTime, schedulingTime, machineNumber, scheduledBy);
		
		addToEventQueue(newStage3Step1FinishedEvent);
	}

	public void decreaseStage3Step1FinishedEventCounter(int machineNumber) {
		decreaseEventCounter(stage3Step1FinishedCounter,machineNumber);
	}
	
	public void scheduleStage3Step2FinishedEvent(int machineNumber, int processingTime, String scheduledBy) {
		increaseEventCounter(stage3Step2FinishedCounter,machineNumber);
		sanityCheck(!getMachineStage3(machineNumber).machineIsEmpty());
		/*
		 * Add a new Stage3Step1Finished Event to the event queue
		 */
		int schedulingTime = currentTime;
		int supposedFinishingTime = schedulingTime + processingTime;
		Event newStage3Step2FinishedEvent = new Stage3Step2Finished(supposedFinishingTime, schedulingTime, machineNumber, scheduledBy);
		
		addToEventQueue(newStage3Step2FinishedEvent);
	}

	public void decreaseStage3Step2FinishedEventCounter(int machineNumber) {
		decreaseEventCounter(stage3Step2FinishedCounter,machineNumber);
	}
	
	public void scheduleStage3Step3FinishedEvent(int machineNumber, int processingTime, String scheduledBy) {
		increaseEventCounter(stage3Step3FinishedCounter,machineNumber);
		sanityCheck(!getMachineStage3(machineNumber).machineIsEmpty());
		/*
		 * Add a new Stage3Step1Finished Event to the event queue
		 */
		int schedulingTime = currentTime;
		int supposedFinishingTime = schedulingTime + processingTime;
		Event newStage3Step3FinishedEvent = new Stage3Step3Finished(supposedFinishingTime, schedulingTime, machineNumber, scheduledBy);
		
		addToEventQueue(newStage3Step3FinishedEvent);
	}

	public void decreaseStage3Step3FinishedEventCounter(int machineNumber) {
		decreaseEventCounter(stage3Step3FinishedCounter,machineNumber);
	}
	
	public void scheduleStage4Finished(int machineNumber,int processingTime, String scheduledBy) {
		increaseEventCounter(stage4FinishedCounter,machineNumber);
		sanityCheck(!getMachineStage4(machineNumber).machineIsEmpty());
		/*
		 * Add a new Stage4Finished Event to the event queue
		 */
		int schedulingTime = currentTime;
		int supposedFinishingTime = schedulingTime + processingTime;
		Event newStage4FinishedEvent = new Stage4Finished(supposedFinishingTime, schedulingTime, machineNumber, scheduledBy);
		
		// one dvd printed, so less toner
		getMachineStage4(machineNumber).decreaseToner();
		addToEventQueue(newStage4FinishedEvent);
	}

	public void decreaseStage4FinishedEventCounter(int machineNumber) {
		decreaseEventCounter(stage4FinishedCounter,machineNumber);
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
			crash();
		}
		counter[machineNumber-1]--;
	}


	public void scheduleCBFinishedEvent(int machineNumber, int processingTime, int dvdID, String scheduledBy) {
		
		// add if needed
		if(conveyorBeltFinishedCounter.get(machineNumber-1).get(dvdID) == null) {
			conveyorBeltFinishedCounter.get(machineNumber-1).put(dvdID,0);
		}
		if(conveyorBeltFinishedCounter.get(machineNumber-1).get(dvdID)!=0) {
			try {
				throw new EventAlreadyInQueueException();
			} catch (EventAlreadyInQueueException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		int newValue = conveyorBeltFinishedCounter.get(machineNumber-1).get(dvdID) +1;
		
		conveyorBeltFinishedCounter.get(machineNumber-1).put(dvdID,newValue);
		
		int schedulingTime = currentTime;
		int supposedFinishingTime = schedulingTime + processingTime;
		Event newCBFinishedEvent = new CBFinished(supposedFinishingTime, schedulingTime, machineNumber, dvdID, scheduledBy);
		
		addToEventQueue(newCBFinishedEvent);
	}
	
	public void decreaseConveyorBeltFinishedCounter(int machineNumber, int dvdID) {
		if(conveyorBeltFinishedCounter.get(machineNumber-1).get(dvdID) != 1) {
			crash();
		}
		
		int newValue = conveyorBeltFinishedCounter.get(machineNumber-1).get(dvdID) - 1;
		
		conveyorBeltFinishedCounter.get(machineNumber-1).put(dvdID,newValue);
	}


	public void sanityCheck(boolean validity) {
		if(!validity)
			crash();
	}
	private void crash() {
		try {
			throw new Exception();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}


}
