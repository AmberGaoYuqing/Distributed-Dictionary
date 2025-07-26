package client;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import java.awt.*;

public class ClientUI extends Thread {

	private static JFrame frame;
	
	private Client client;
	private JTextField wordField;
	private JTextField meaningField;
	private JTextField updateMeaningField;
	private JTextArea outputArea;
	private JLabel userDictionaryLabel;

	/**
	 * Create the application.
	 */
	public ClientUI(Client client) {
		this.client = client;
		if(!this.client.isAlive()) {
			this.client.start();
		}
		initialize();
	}

	public JFrame getFrame() {
		return frame;
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 576, 448);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		
		wordField = new JTextField();
		wordField.setBounds(180, 102, 174, 21);
		frame.getContentPane().add(wordField);
		wordField.setColumns(10);

		outputArea = new JTextArea();
		outputArea.setBounds(180, 291, 341, 75);
		frame.getContentPane().add(outputArea);

		meaningField = new JTextField();
		meaningField.setBounds(182, 168, 341, 21);
		frame.getContentPane().add(meaningField);
		meaningField.setColumns(10);

		updateMeaningField = new JTextField();
		updateMeaningField.setBounds(182, 235, 341, 21);
		frame.getContentPane().add(updateMeaningField);
		updateMeaningField.setColumns(10);

		JButton addButton = new JButton("Add");
		addButton.addActionListener(new ActionHandler(ActionHandler.ActionType.ADD, wordField, meaningField, outputArea, client));
		addButton.setBounds(21, 133, 130, 23);
		frame.getContentPane().add(addButton);
		
		JButton queryButton = new JButton("Query");
		queryButton.addActionListener(new ActionHandler(ActionHandler.ActionType.QUERY, wordField, meaningField, updateMeaningField,outputArea, client));
		queryButton.setBounds(21, 101, 130, 23);
		frame.getContentPane().add(queryButton);

		JButton deleteButton = new JButton("Delete Word");
		deleteButton.addActionListener(new ActionHandler(ActionHandler.ActionType.DELETE, wordField, meaningField,updateMeaningField, outputArea, client));
		deleteButton.setBounds(21, 167, 130, 23);
		frame.getContentPane().add(deleteButton);

		JButton addMeaningButton = new JButton("Add Meaning");
		addMeaningButton.addActionListener(new ActionHandler(ActionHandler.ActionType.ADD_MEANING, wordField, meaningField,updateMeaningField, outputArea, client));
		addMeaningButton.setBounds(21, 200, 130, 23);
		frame.getContentPane().add(addMeaningButton);

		JButton updateButton = new JButton("Update");
		updateButton.addActionListener(new ActionHandler(ActionHandler.ActionType.UPDATE, wordField, meaningField, updateMeaningField,outputArea, client));
		updateButton.setBounds(21, 233, 130, 23);
		frame.getContentPane().add(updateButton);

		userDictionaryLabel = new JLabel("User Dictionary");
		userDictionaryLabel.setBounds(182, 10, 198, 49);
		userDictionaryLabel.setFont(new Font("Consolas", Font.PLAIN, 22));
		frame.getContentPane().add(userDictionaryLabel);

		JLabel inputLabel = new JLabel("Input:");
		inputLabel.setBounds(180, 77, 44, 15);
		frame.getContentPane().add(inputLabel);

		JLabel meaningLabel = new JLabel("Meaning(s):");
		meaningLabel.setBounds(182, 137, 120, 15);
		frame.getContentPane().add(meaningLabel);

		JLabel updateMeaningLabel = new JLabel("Update meaning(s):");
		updateMeaningLabel.setBounds(180, 206, 136, 15);
		frame.getContentPane().add(updateMeaningLabel);

		JLabel outputLabel = new JLabel("Output:");
		outputLabel.setBounds(180, 262, 54, 15);
		frame.getContentPane().add(outputLabel);
	}

}
