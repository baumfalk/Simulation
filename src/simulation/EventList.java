package simulation;

import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class EventList extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2608096954971610548L;
	private JPanel contentPane;
	private DefaultListModel<String> listModel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					EventList frame = new EventList();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public EventList() {
		setTitle("DVD Factory Simulation - Event List");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 320, 484);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblEventList = new JLabel("Event List:");
		lblEventList.setBounds(20, 11, 70, 14);
		contentPane.add(lblEventList);
		listModel = new DefaultListModel<String>();
		JList list = new JList();
		list.setModel(listModel);
		
		list.setBounds(20, 36, 274, 399);
		contentPane.add(list);
	}
	
	public void newList(ArrayList<String> stringList)
	{
		listModel.clear();
		for(String s : stringList) {
			listModel.addElement(s);
		}
	}

}
