package simulation;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.OverlayLayout;
import javax.swing.Timer;

import machines.ConveyorBelt;
import machines.MachineStage1;
import machines.MachineStage2;
import machines.MachineStage3;

public class SimulationGUI {

	private JFrame frmDvdFactorySimulation;
	private static EventList eventListWindow;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	
	private Timer timer; 
	
	private Simulation simulation;
	private ArrayList<JLabel> stage1Labels;
	private ArrayList<JLabel> stage2Labels;
	private ArrayList<JLabel> stage3Labels;
	private ArrayList<JLabel> stage4Labels;
	private ArrayList<JLabel> conveyorBeltLabels;
	private ArrayList<JLabel> buffersOne;
	private ArrayList<JLabel> buffersTwo;
	private ArrayList<JLabel> buffersThree;
	private ActionListener listener;
	private JLabel lblTime;
	private JButton btnPause;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					SimulationGUI window = new SimulationGUI();
					window.frmDvdFactorySimulation.setVisible(true);
					eventListWindow = new EventList();
					eventListWindow.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws IOException 
	 */
	public SimulationGUI() throws IOException {
		initialize();
	}

	
	private void updateGUI()
	{
		for(int i=1;i<=4;i++) {
			MachineStage1 m1 = simulation.getMachineStage1(i);
			stage1Labels.get(i-1).setText(m1.state.name());
			
		}
		for(int i=1;i<=2;i++) {
			MachineStage2 m2 = simulation.getMachineStage2(i);
			stage2Labels.get(i-1).setText(m2.state.name());
			buffersOne.get(i-1).setText(m2.leftBuffer().currentDVDCount()+"");
			
			ConveyorBelt cb = simulation.getConveyorBelt(i);
			conveyorBeltLabels.get(i-1).setText(cb.state.name());
			buffersTwo.get(i-1).setText(cb.rightBuffer().currentDVDCount()+"");
			
			MachineStage3 m3 = simulation.getMachineStage3(i);
			stage3Labels.get(i-1).setText(m3.state.name());
			buffersThree.get(i-1).setText(m3.rightBuffer(i-1).currentDVDCount()+"");

		}
		eventListWindow.newList(simulation.getEventListString());
		lblTime.setText("Time: " + simulation.getCurrentTime());
		
		eventListWindow.repaint();
		frmDvdFactorySimulation.repaint();
	}
	/**
	 * Initialize the contents of the frame.
	 * @throws IOException 
	 */
	private void initialize() throws IOException {
		initWindow();
		initButtons();
		
		initBuffers();
		initStage1();
		initConveyorBelts();
		initStage2();
		initStage3();
		initStage4();
	
		initImage();
	}

	private void initWindow() {
		frmDvdFactorySimulation = new JFrame();
		frmDvdFactorySimulation.setTitle("DVD Factory Simulation");
		frmDvdFactorySimulation.setBounds(100, 100, 991, 653);
		frmDvdFactorySimulation.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDvdFactorySimulation.getContentPane().setLayout(null);
	}

	private void initImage() {
		JPanel panel = new JPanel();
		panel.setBounds(10, 45, 955, 559);
		frmDvdFactorySimulation.getContentPane().add(panel);
		panel.setLayout(new OverlayLayout(panel));
		
		JLabel lblFlowchart = new JLabel();
		lblFlowchart.setIcon(new ImageIcon("./flowchart.png"));
		panel.add(lblFlowchart);
		lblFlowchart.setBounds(10, 45, 955, 559);
	}

	private void initButtons() {
		
		lblTime = new JLabel("Time:");
		lblTime.setBounds(853, 15, 112, 14);
		frmDvdFactorySimulation.getContentPane().add(lblTime);
		listener = new ActionListener() {
		    @Override 
		    public void actionPerformed(ActionEvent e) {
		    	nextSimulationState();
		    	
		    }
		};
		timer = new Timer(1, listener);
		final JLabel lblPaused = new JLabel("Paused");
		lblPaused.setBounds(797, 15, 46, 14);
		lblPaused.setBackground(Color.YELLOW);
		lblPaused.setOpaque(true);
		frmDvdFactorySimulation.getContentPane().add(lblPaused);
		
		
		
		btnPause = new JButton("Pause");
		btnPause.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(btnPause.getText().equals("Pause")) {
					btnPause.setText("Unpause");
					timer.stop();
					lblPaused.setText("Paused");
					lblPaused.setBackground(Color.YELLOW);
					
				} else {
					btnPause.setText("Pause");
					timer.start();
					lblPaused.setText("Running");
					lblPaused.setBackground(Color.GREEN);
				}
			}
		});
		btnPause.setBounds(109, 11, 89, 23);
		frmDvdFactorySimulation.getContentPane().add(btnPause);
		
		
		
		final JButton btnStart = new JButton("Start");
		
		btnStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				simulation = new Simulation(60*60*24*30*6, 20, 20);
				updateGUI();
				timer.start();
				btnPause.setText("Pause");
				btnStart.setText("Restart");
				lblPaused.setText("Running");
				lblPaused.setBackground(Color.GREEN);
			}
		});
		btnStart.setBounds(10, 11, 89, 23);
		frmDvdFactorySimulation.getContentPane().add(btnStart);
		
		JButton btnNextEvent = new JButton("Next Event");
		btnNextEvent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				nextSimulationState();
			}
		});
		btnNextEvent.setBounds(208, 11, 97, 23);
		frmDvdFactorySimulation.getContentPane().add(btnNextEvent);
		
		JLabel lblSimulationSpeed = new JLabel("Simulation Speed:");
		lblSimulationSpeed.setBounds(315, 15, 119, 14);
		frmDvdFactorySimulation.getContentPane().add(lblSimulationSpeed);
		
		JRadioButton rdbtn10ms = new JRadioButton("10 ms/event");
		rdbtn10ms.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setTimerTime(10);
			}
		});
		rdbtn10ms.setBounds(440, 11, 95, 23);
		rdbtn10ms.setSelected(true);
		buttonGroup.add(rdbtn10ms);
		frmDvdFactorySimulation.getContentPane().add(rdbtn10ms);
		
		JRadioButton rdbtn50ms = new JRadioButton("50 ms/event");
		rdbtn50ms.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setTimerTime(50);
			}
		});
		rdbtn50ms.setBounds(537, 11, 105, 23);
		buttonGroup.add(rdbtn50ms);
		frmDvdFactorySimulation.getContentPane().add(rdbtn50ms);
		
		JRadioButton rdbtn300ms = new JRadioButton("300 ms/event");
		rdbtn300ms.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setTimerTime(300);
			}
		});
		rdbtn300ms.setBounds(644, 11, 102, 23);
		buttonGroup.add(rdbtn300ms);
		frmDvdFactorySimulation.getContentPane().add(rdbtn300ms);
		
		JLabel lblState = new JLabel("State:");
		lblState.setBounds(752, 15, 35, 14);
		frmDvdFactorySimulation.getContentPane().add(lblState);
		
	
	}
	
	private void initStage1() {
		stage1Labels = new ArrayList<JLabel>();
		
		JLabel lblS1M1State = new JLabel("S1M1 State");
		lblS1M1State.setBounds(65, 149, 133, 14);
		frmDvdFactorySimulation.getContentPane().add(lblS1M1State);
		stage1Labels.add(lblS1M1State);
		
		JLabel lblS1M2State = new JLabel("S1M2 State");
		lblS1M2State.setBounds(65, 258, 133, 14);
		frmDvdFactorySimulation.getContentPane().add(lblS1M2State);
		stage1Labels.add(lblS1M2State);
		
		JLabel lblS1M3State = new JLabel("S1M3 State");
		lblS1M3State.setBounds(65, 366, 133, 14);
		frmDvdFactorySimulation.getContentPane().add(lblS1M3State);
		stage1Labels.add(lblS1M3State);
		
		JLabel lblS1M4State = new JLabel("S1M4 State");
		lblS1M4State.setBounds(65, 471, 133, 14);
		frmDvdFactorySimulation.getContentPane().add(lblS1M4State);
		stage1Labels.add(lblS1M4State);
	}
	
	private void initBuffers() {
		buffersOne = new ArrayList<JLabel>();
		JLabel lblL1B1 = new JLabel("L1B1 State");
		lblL1B1.setBounds(197, 202, 76, 14);
		frmDvdFactorySimulation.getContentPane().add(lblL1B1);
		buffersOne.add(lblL1B1);
		
		JLabel lblL1B2 = new JLabel("L1B2 State");
		lblL1B2.setBounds(197, 421, 76, 14);
		frmDvdFactorySimulation.getContentPane().add(lblL1B2);
		buffersOne.add(lblL1B2);
		
		buffersTwo = new ArrayList<JLabel>();
		JLabel lblL2B1 = new JLabel("L2B1 State");
		lblL2B1.setBounds(500, 202, 76, 14);
		frmDvdFactorySimulation.getContentPane().add(lblL2B1);
		buffersTwo.add(lblL2B1);
		
		JLabel lblL2B2 = new JLabel("L2B2 State");
		lblL2B2.setBounds(500, 421, 76, 14);
		frmDvdFactorySimulation.getContentPane().add(lblL2B2);
		buffersTwo.add(lblL2B2);
		
		buffersThree = new ArrayList<JLabel>();
		JLabel lblL3B1 = new JLabel("L3B1 State");
		lblL3B1.setBounds(727, 202, 76, 14);
		frmDvdFactorySimulation.getContentPane().add(lblL3B1);
		buffersThree.add(lblL3B1);
		
		JLabel lblL3B2 = new JLabel("L3B2 State");
		lblL3B2.setBounds(727, 508, 76, 14);
		frmDvdFactorySimulation.getContentPane().add(lblL3B2);
		buffersThree.add(lblL3B2);
	}
	
	private void initConveyorBelts() {
		conveyorBeltLabels = new ArrayList<JLabel>();
		JLabel lblCB1 = new JLabel("CB1 State");
		lblCB1.setBounds(392, 202, 76, 14);
		frmDvdFactorySimulation.getContentPane().add(lblCB1);
		conveyorBeltLabels.add(lblCB1);
		
		JLabel lblCB2 = new JLabel("CB2 State");
		lblCB2.setBounds(392, 421, 76, 14);
		frmDvdFactorySimulation.getContentPane().add(lblCB2);
		conveyorBeltLabels.add(lblCB2);
	}
	
	private void initStage2() {
		stage2Labels = new ArrayList<JLabel>();
		JLabel lblS2M1State = new JLabel("S2M1 State");
		lblS2M1State.setBounds(283, 202, 76, 14);
		frmDvdFactorySimulation.getContentPane().add(lblS2M1State);
		stage2Labels.add(lblS2M1State);
		
		JLabel lblS2M2State = new JLabel("S2M2 State");
		lblS2M2State.setBounds(283, 421, 76, 14);
		frmDvdFactorySimulation.getContentPane().add(lblS2M2State);
		stage2Labels.add(lblS2M2State);
	}
	
	private void initStage3() {
		
		stage3Labels = new ArrayList<JLabel>();
		JLabel lblS3M1State = new JLabel("S3M1 State");
		lblS3M1State.setBounds(606, 202, 76, 14);
		frmDvdFactorySimulation.getContentPane().add(lblS3M1State);
		stage3Labels.add(lblS3M1State);
		
		JLabel lblS3M2State = new JLabel("S3M2 State");
		lblS3M2State.setBounds(606, 508, 76, 14);
		frmDvdFactorySimulation.getContentPane().add(lblS3M2State);
		stage3Labels.add(lblS3M2State);
	}
	
	private void initStage4() {
			
			stage4Labels = new ArrayList<JLabel>();
			JLabel lblS4M1State = new JLabel("S4M1 State");
			lblS4M1State.setBounds(832, 202, 110, 14);
			frmDvdFactorySimulation.getContentPane().add(lblS4M1State);
			stage4Labels.add(lblS4M1State);
			
			JLabel lblS4M2State = new JLabel("S4M2 State");
			lblS4M2State.setBounds(838, 421, 119, 14);
			frmDvdFactorySimulation.getContentPane().add(lblS4M2State);
			stage4Labels.add(lblS4M2State);
		}
	
	private void nextSimulationState() {
		simulation.nextStep();
		updateGUI();
	}
	
	private void setTimerTime(int time) {
		timer.setDelay(time);
		timer.restart();
		if(!btnPause.getText().equals("Pause"))
			timer.stop();
	}
}
