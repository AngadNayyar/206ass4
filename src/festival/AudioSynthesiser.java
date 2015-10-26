package festival;

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
import javax.swing.JCheckBox;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/**
 * This class is where the other classes in the "festival" package are utilized from.
 * This package allows for the festival functionalities of the code to occur.
 */
public class AudioSynthesiser extends JFrame {
	//Declare fields
	private JPanel contentPane;
	private JTextField textField;
	private boolean femaleVoice;
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
	
	//Make this frame visible.
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

	//This creates the frame and GUI components for the audio creator.
	public AudioSynthesiser() {
		
		//Initializes the GUI
		setResizable(false);
		setBounds(250, 250, 660, 384);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//Text to display any warning messages to the user.
		final JLabel label = new JLabel("");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setForeground(Color.RED);
		label.setBounds(132, 160, 332, 14);
		contentPane.add(label);
		label.setVisible(false);
		
		textField = new JTextField();
		textField.setBounds(122, 128, 456, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		//Label that informs the user what to do
		JLabel lblPleaseEnterA = new JLabel("Please enter text to synthesize (20 words max)");
		lblPleaseEnterA.setHorizontalAlignment(SwingConstants.CENTER);
		lblPleaseEnterA.setBounds(122, 77, 400, 35);
		contentPane.add(lblPleaseEnterA);
		
		//Gets the users input from the text field which then passed into
		//Only allows for maximum 20 words to ensure the synthesized speech does not exceed the length of the video
		//Uses swingworker to playback the audio
		JButton btnPlaybackText = new JButton("Playback");
		btnPlaybackText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = textField.getText();
				if (wordCount(s) == 0){ //If no text is entered, show warning
					label.setText("Please enter some text");
					label.setVisible(true);
				}else if (wordCount(s) > 20){ //If too many words are entered, show warning
					label.setText("Please enter at most 20 words");
					label.setVisible(true);
				}else{	
					if (femaleVoice){ //if female voice is selected, use femalevoiceworker to play audio
						FemaleVoiceWorker female = new FemaleVoiceWorker(s, btnPlaybackText);
						female.execute();
					} else { //otherwise use ListenMp3worker to play default voice
						ListenMp3Worker listen = new ListenMp3Worker(s, btnPlaybackText);
						listen.execute();
					}
					label.setVisible(false); //remove warning label
				}
			}
		});
		btnPlaybackText.setBounds(122, 222, 128, 48);
		contentPane.add(btnPlaybackText);
		
		//Allows the saving of the audio. Warnings are shown if text is none or too long.
		//The user can choose the name of the audio, and location, with JFileChooser.
		JButton btnSaveAudio = new JButton("Save Audio");
		btnSaveAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = textField.getText();
				if (wordCount(s) == 0){ //If no text is entered, show warning
					label.setText("Please enter some text");
					label.setVisible(true);
				}else if (wordCount(s) > 20){ //If too many words are entered, show warning
					label.setText("Please enter at most 20 words");
					label.setVisible(true);
				}else{
					fileSaver = new JFileChooser(); //Filechooser to select file name and location
					FileFilter savefilter = new FileNameExtensionFilter("Audio Files", new String[] {"mp3","wav"});
					fileSaver.setFileFilter(savefilter);
					fileSaver.setDialogTitle("Choose a name and location");
					fileSaver.setFileSelectionMode(JFileChooser.FILES_ONLY);
					int result = fileSaver.showSaveDialog(null);
			        if (result == JFileChooser.APPROVE_OPTION){ //If user accepts new name and location
						File fileSave = fileSaver.getSelectedFile();
						fileSaver.setVisible(false);
						if(femaleVoice){ //If female voice is selected, create female audio
							FemaleSaveWorker femaleSave = new FemaleSaveWorker(s, fileSave);
							femaleSave.execute();
						} else { //otherwise create default audio
							CreateMp3Worker createmp3 = new CreateMp3Worker(s, fileSave);
							createmp3.execute();
						}
						setVisible(false);
						label.setVisible(false);
					}else if(result == JFileChooser.CANCEL_OPTION){
						fileSaver.setVisible(false); //close file chooser if cancel is chosen
					}
				}
				
			}
		});
		btnSaveAudio.setBounds(283, 222, 128, 48);
		contentPane.add(btnSaveAudio);
		
		
		//Closes the GUI window and returns to the main application
		JButton btnCancelTTS = new JButton("Cancel");
		btnCancelTTS.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		btnCancelTTS.setBounds(449, 222, 110, 48);
		contentPane.add(btnCancelTTS);
		
		//Checkbox to allow female voice to be used.
		JCheckBox chckbxFemaleVoice = new JCheckBox("Female Voice");
		chckbxFemaleVoice.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (chckbxFemaleVoice.isSelected()){
					femaleVoice = true;
				} else {
					femaleVoice = false;
				}
			}
		});
		chckbxFemaleVoice.setBounds(142, 172, 221, 41);
		contentPane.add(chckbxFemaleVoice);
		
		//End of GUI components
	
		
	}
	
	
	
	
}
