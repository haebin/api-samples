package com.google.api.services.samples.youtube.cmdline.partner.cuetone.audio;

import java.util.Date;

import javax.swing.JTextArea;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;

import com.google.api.services.samples.youtube.cmdline.partner.LiveAdsManager;

public class YtnSignalHandler implements PitchDetectionHandler {
	//private float echo = 0;
	//private float echoProbability = 0;
	//private double echoRms = 0;
	private double echoTimeStamp = 0;
	
	//private float echoCnt = 0;
	//private float pitchDiff = 5;
	private boolean rawLog = true;
	
	private int signalIdx = 0;
	private double commandWindow = 1; // in sec.
	private double signalWindow = 0.04; // in sec.
	private char signal[] = new char[4];
	
	private static final char[] ADS_START = "111*".toCharArray();
	private static final char[] ADS_END = "111#".toCharArray();
	
	private final LiveAdsManager adsManager;
	private final JTextArea textArea;
	
	
	public YtnSignalHandler(LiveAdsManager adsManager, JTextArea textArea) {
		this.textArea = textArea;
		this.adsManager = adsManager;
	}
	
	private void log(String message) {
		textArea.append(message);
		textArea.setCaretPosition(textArea.getDocument().getLength());
	}
	
	@Override
	public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
		if(pitchDetectionResult.getPitch() != -1){
			double timeStamp = audioEvent.getTimeStamp();
			float pitch = pitchDetectionResult.getPitch();
			float probability = pitchDetectionResult.getProbability();
			double rms = audioEvent.getRMS() * 100;
			
			if(rawLog) {
				String message = String.format("Pitch detected at %.2fs: %.2fHz ( %.2f probability, RMS: %.5f )\n", 
						timeStamp, pitch, probability, rms);
				log(message);
			}
			
			if(pitch < 900 && pitch > 150) {
				double timeGap = timeStamp - echoTimeStamp;
				echoTimeStamp = timeStamp;
				
				if(timeGap > commandWindow) {
					signalIdx = 0; // new signal starts.
					signal[0] = signal[1] = signal[2] = signal[3] = ' ';
					signal[0] = decodeDtmf(pitch);
					
					//if(rawLog) {
						String filtered = String.format("[%d] = '%c' Pitch detected at %.2fs: %.2fHz\n", 
								signalIdx,signal[signalIdx%signal.length], timeStamp, pitch);
						log(filtered);
					//}
				} else if(timeGap > signalWindow) {
					signalIdx++;
					signal[signalIdx%signal.length] = decodeDtmf(pitch);
					//if(rawLog) {
						String filtered = String.format("[%d] = '%c' Pitch detected at %.2fs: %.2fHz\n", 
								signalIdx,signal[signalIdx%signal.length], timeStamp, pitch);
						log(filtered);
					//}
					
					if(signal.length - 1 == signalIdx) {
						// complete signal
						log("--> " + new Date() + " = " + new String(signal) + "\n\n\n");
						
						//processCommand();
					}
				}
			}
		}
	}
	
	private void processCommand() {
		for(int i=0; i<signal.length; i++) {
			if(ADS_START[i] != signal[i]) {
				return;
			}
		}
		
		try {
			adsManager.insertAds();
		} catch (Exception e) {
			log(e.getMessage());
		}
	}
	
	float N1_2 = 172;
	float N1_1 = 239; 
	float N1 = 621, N2 = 674, N3 = 730, A = 798;
	float N4 = 397, N5 = 691, N6 = 745, B = 808;
	float N7 = 410, N8 = 439, N9 = 763, C = 824;
	float Na = 306, N0 = 453, Ns = 486, D = 844;
	
	// 1:621~5, 239 
	// 1:625, 2:674, 3:730, A:798
	// 4:397, 5:691, 6:745, B:808
	// 7:410, 8:439, 9:763, C:824
	// *:306, 0:453, #:486, D:844
	private char decodeDtmf(float echo) {
		if(is(echo, N1)) return '1';
		if(is(echo, N1_1)) return '1';
		if(is(echo, N1_2)) return '1';	
		if(is(echo, Na)) return '*';
		if(is(echo, Ns)) return '#';
		
		if(is(echo, N2)) return '2';
		if(is(echo, N3)) return '3';
		if(is(echo, N4)) return '4';
		if(is(echo, N5)) return '5';
		if(is(echo, N6)) return '6';
		if(is(echo, N7)) return '7';
		if(is(echo, N8)) return '8';
		if(is(echo, N9)) return '9';
		if(is(echo, N0)) return '0';
		
		if(is(echo, A)) return 'A';
		if(is(echo, B)) return 'B';
		if(is(echo, C)) return 'C';
		if(is(echo, D)) return 'D';
		return ' ';
	}
	
	float bound = 7;
	private boolean is(float pitch, float code) {
		if(code-bound < pitch && pitch < code+bound) {
			return true;
		}
		return false;
	}
}
