package server;

import javax.swing.*;
import java.awt.*;

public class ServerGUI extends Thread {

	private JFrame frame;
	public static JTextArea outputArea;
	public static JTextField textField;
	public static DefaultListModel<String> userListModel = new DefaultListModel<>();
	public static JList<String> userList = new JList<>(userListModel);
	public static JTextArea logArea = new JTextArea();

	/**
	 * Create the application.
	 */
	public ServerGUI() {
		initialize();
	}
	
	public void run() {
		try {
			ServerGUI serverGUI = new ServerGUI();
			serverGUI.frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 550, 550);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		textField = new JTextField();
		textField.setEditable(false);
		textField.setBounds(50, 80, 120, 26);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Client Count");
		lblNewLabel.setBounds(50, 50, 80, 20);
		frame.getContentPane().add(lblNewLabel);
		
		outputArea = new JTextArea();
		outputArea.setEditable(false);
		outputArea.setLineWrap(true);
		// 创建滚动面板并包含 textArea
		JScrollPane scrollPane = new JScrollPane(outputArea);
		scrollPane.setBounds(200, 150, 300, 200);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		frame.getContentPane().add(scrollPane);

		JLabel lblUsers = new JLabel("Connected Users:");
		lblUsers.setBounds(50, 120, 150, 16);
		frame.getContentPane().add(lblUsers);

		userList.setBounds(50, 150, 120, 200); // 调整大小和位置
		frame.getContentPane().add(userList);

		JLabel lblLog = new JLabel("Server Logs");
		lblLog.setBounds(50, 370, 150, 20);
		frame.getContentPane().add(lblLog);

		logArea.setEditable(false);
		logArea.setLineWrap(true);
		JScrollPane logScrollPane = new JScrollPane(logArea);
		logScrollPane.setBounds(50, 400, 450, 80);
		frame.getContentPane().add(logScrollPane);

		JLabel serverLabel = new JLabel("Admin Dictionary");
		serverLabel.setBounds(182, 10, 198, 49);
		serverLabel.setFont(new Font("Consolas", Font.PLAIN, 22));
		frame.getContentPane().add(serverLabel);

		JLabel dictionaryLabel = new JLabel("Dictionary");
		dictionaryLabel.setBounds(200, 120, 150, 16);
		frame.getContentPane().add(dictionaryLabel);


	}

}
