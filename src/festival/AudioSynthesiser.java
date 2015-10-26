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

public class AudioSynthesiser extends JFrame {
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
		setBounds(250, 250, 660, 384);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//GUI components
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
					if (femaleVoice){
						FemaleVoiceWorker female = new FemaleVoiceWorker(s, btnPlaybackText);
						female.execute();
					} else {
						ListenMp3Worker listen = new ListenMp3Worker(s, btnPlaybackText);
						listen.execute();
					}
					label.setVisible(false);
				}
			}
		});
		btnPlaybackText.setBounds(122, 222, 128, 48);
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
						if(femaleVoice){
							FemaleSaveWorker femaleSave = new FemaleSaveWorker(s, fileSave);
							femaleSave.execute();
						} else {
							CreateMp3Worker createmp3 = new CreateMp3Worker(s, fileSave);
							createmp3.execute();
						}
						setVisible(false);
						label.setVisible(false);
					}else if(result == JFileChooser.CANCEL_OPTION){
						fileSaver.setVisible(false);	
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
