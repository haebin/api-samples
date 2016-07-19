/*
*      _______                       _____   _____ _____  
*     |__   __|                     |  __ \ / ____|  __ \ 
*        | | __ _ _ __ ___  ___  ___| |  | | (___ | |__) |
*        | |/ _` | '__/ __|/ _ \/ __| |  | |\___ \|  ___/ 
*        | | (_| | |  \__ \ (_) \__ \ |__| |____) | |     
*        |_|\__,_|_|  |___/\___/|___/_____/|_____/|_|     
*                                                         
* -------------------------------------------------------------
*
* TarsosDSP is developed by Joren Six at IPEM, University Ghent
*  
* -------------------------------------------------------------
*
*  Info: http://0110.be/tag/TarsosDSP
*  Github: https://github.com/JorenSix/TarsosDSP
*  Releases: http://0110.be/releases/TarsosDSP/
*  
*  TarsosDSP includes modified source code by various authors,
*  for credits and info, see README.
* 
*/


package com.google.api.services.samples.youtube.cmdline.partner.cuetone;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.TarsosDSPAudioFloatConverter;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;
import be.tarsos.dsp.io.jvm.AudioPlayer;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.DTMF;

/**
 * An example of DTMF ( Dual-tone multi-frequency signaling ) decoding with the Goertzel algorithm.
 * @author Joren Six
 */
public class DialPad extends JFrame implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1143769091770146361L;
	
	private final int stepSize = 256;
	
	private KeyAdapter keyAdapter = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent event) {
			if(DTMF.isDTMFCharacter(event.getKeyChar())){
				try {
					process(event.getKeyChar());
				} catch (UnsupportedAudioFileException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (LineUnavailableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
	
	public DialPad(){
		this.getContentPane().setLayout(new BorderLayout(5,3));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("DialPad");
		
		JPanel dailPad = new JPanel(new GridLayout(4,4));
		//dailPad.setBorder(new TitledBorder("DailPad"));
		for(int row = 0 ; row < DTMF.DTMF_CHARACTERS.length ; row++){
			for(int col = 0 ; col < DTMF.DTMF_CHARACTERS[row].length ; col++){
				JButton numberButton = new JButton(DTMF.DTMF_CHARACTERS[row][col]+"");
				numberButton.addActionListener(this);
				numberButton.addKeyListener(keyAdapter);
				dailPad.add(numberButton);
			}
		}
		this.addKeyListener(keyAdapter);
		dailPad.addKeyListener(keyAdapter);
		
		this.add(dailPad,BorderLayout.CENTER);
	}

	public static void main(String...strings){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					//ignore failure to set default look & feel;
				}
				JFrame frame = new DialPad();
				frame.pack();
				frame.setSize(200,220);
				frame.setVisible(true);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JButton button = ((JButton) event.getSource());
		//System.out.println(button.getText().charAt(0));
		try {
			process(button.getText().charAt(0));
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Process a DTMF character: generate sound and decode the sound.
	 * @param character The character.
	 * @throws UnsupportedAudioFileException
	 * @throws LineUnavailableException
	 */
	public void process(char character) throws UnsupportedAudioFileException, LineUnavailableException{
		final float[] floatBuffer = DTMF.generateDTMFTone(character);		
		final AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
		//JVMAudioInputStream.toTarsosDSPFormat(format);
		final TarsosDSPAudioFloatConverter converter = TarsosDSPAudioFloatConverter.getConverter(JVMAudioInputStream.toTarsosDSPFormat(format));
		final byte[] byteBuffer = new byte[floatBuffer.length * format.getFrameSize()];
		converter.toByteArray(floatBuffer, byteBuffer);
		final ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer);		
		final AudioInputStream inputStream = new AudioInputStream(bais, format,floatBuffer.length);
		final TarsosDSPAudioInputStream stream = new JVMAudioInputStream(inputStream);
		final AudioDispatcher dispatcher = new AudioDispatcher(stream, stepSize, 0);		
		//dispatcher.addAudioProcessor(goertzelAudioProcessor);
		dispatcher.addAudioProcessor(new AudioPlayer(format));
		new Thread(dispatcher).start();
		
	}
}
