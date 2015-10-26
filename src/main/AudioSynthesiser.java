package main;

import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.awt.Color;

public class AudioSynthesiser extends JFrame {
	private JPanel contentPane;
	private JTextField textField;
	//BackgroundWorker b;
	protected JFileChooser fileSaver;
	

	
	//Method that returns the amount of words in a string
	public int wordCount(String s){
		String t =  s.trim();
		if (t.isEmpty()){
			return 0;
		}
		int words = t.split("\\s+").length;
		return words;
		
		
	}
	
	/**
	 * Launch the application.
	 */
	public static void launch(){
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AudioSynthesiser frame = new AudioSynthesiser();
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
	public AudioSynthesiser() {
		
		//Initializes the gui
		setResizable(false);
		setBounds(250, 250, 589, 337);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//GUI components
		final JLabel label = new JLabel("");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setForeground(Color.RED);
		label.setBounds(125, 242, 332, 14);
		contentPane.add(label);
		label.setVisible(false);
		
		textField = new JTextField();
		textField.setBounds(58, 128, 456, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		//Label that informs the user what to do
		JLabel lblPleaseEnterA = new JLabel("Please enter text to synthesize (20 words max)");
		lblPleaseEnterA.setHorizontalAlignment(SwingConstants.CENTER);
		lblPleaseEnterA.setBounds(100, 82, 400, 35);
		contentPane.add(lblPleaseEnterA);
		
		JLabel lblAudioWillBe = new JLabel("Audio will be saved to the directory of the jar, as textToSpeech.mp3 ");
		lblAudioWillBe.setForeground(Color.GRAY);
		lblAudioWillBe.setHorizontalAlignment(SwingConstants.CENTER);
		lblAudioWillBe.setBounds(40, 220, 500, 35);
		contentPane.add(lblAudioWillBe);
		
		//Gets the users input from the text field which then passed into
		//the ReplayTTS Method, only allows for maximum 20 words to ensure the synthesized speech
		//does not exceed the length of the video
		JButton btnPlaybackText = new JButton("Playback");
		btnPlaybackText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = textField.getText();
				if (wordCount(s) == 0){
					label.setText("Please enter some text");
					label.setVisible(true);
				}else if (wordCount(s) > 20){
					label.setText("Please enter at most 20 words");
					label.setVisible(true);
				}else{	
					ListenMp3Worker listen = new ListenMp3Worker(s, btnPlaybackText);
					listen.execute();
					label.setVisible(false);
				}
			}
		});
		btnPlaybackText.setBounds(84, 169, 128, 48);
		contentPane.add(btnPlaybackText);
		
		//Again gets the users input and then passes it into the SaveTTS method
		//with the same 20 word limit, it then closes this gui window
		JButton btnSaveAudio = new JButton("Save Audio");
		btnSaveAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = textField.getText();
				if (wordCount(s) == 0){
					label.setText("Please enter some text");
					label.setVisible(true);
				}else if (wordCount(s) > 20){
					label.setText("Please enter at most 20 words");
					label.setVisible(true);
				}else{
					fileSaver = new JFileChooser();
					FileFilter savefilter = new FileNameExtensionFilter("Audio Files", new String[] {"mp3","wav"});
					fileSaver.setFileFilter(savefilter);
					fileSaver.setDialogTitle("Choose a name and location");
					fileSaver.setFileSelectionMode(JFileChooser.FILES_ONLY);
					int result = fileSaver.showSaveDialog(null);
			        if (result == JFileChooser.APPROVE_OPTION){
						File fileSave = fileSaver.getSelectedFile();
						fileSaver.setVisible(false);
						CreateMp3Worker createmp3 = new CreateMp3Worker(fileSave);
						createmp3.execute();
					}else if(result == JFileChooser.CANCEL_OPTION){
						fileSaver.setVisible(false);	
					}
					setVisible(false);
					label.setVisible(false);
				}
				
			}
		});
		btnSaveAudio.setBounds(222, 169, 128, 48);
		contentPane.add(btnSaveAudio);
		
		
		//Closes the GUI window and returns to the main application
		JButton btnCancelTTS = new JButton("Cancel");
		btnCancelTTS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		btnCancelTTS.setBounds(360, 169, 110, 48);
		contentPane.add(btnCancelTTS);
		
		//End of GUI components
	
		
	}
	
	
	
	
}
