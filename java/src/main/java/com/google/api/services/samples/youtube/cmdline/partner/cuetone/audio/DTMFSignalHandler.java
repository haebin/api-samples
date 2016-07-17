package com.google.api.services.samples.youtube.cmdline.partner.cuetone.audio;

import java.util.Arrays;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;

import com.google.api.services.samples.youtube.cmdline.partner.LiveAdsManager;

public class DTMFSignalHandler implements PitchDetectionHandler {
	final static Logger logger = LoggerFactory.getLogger(DTMFSignalHandler.class);
	
	private boolean rawLog = true;
	private double echoTimeStamp = 0;
	
	private int signalIdx = 0;
	private double commandWindow = 1; // in sec.
	private double signalWindow = 0.04; // in sec.
	private char signal[] = {' ', ' ', ' ', ' '};
	
	private static final char[] ADS_START = "111*".toCharArray();
	//private static final String ADS_END = "111#";
	
	private int[] chCounter = new int[17];
	
	private final LiveAdsManager adsManager;
	private final LogViewer logViewer;

	public DTMFSignalHandler(LiveAdsManager adsManager, LogViewer logViewer) {
		this.logViewer = logViewer;
		this.adsManager = adsManager;
	}
	
	int dec(char ch) {
		if(65 <= ch && ch <= 68)
			return ch - 55;
		if(48 <= ch && ch <= 57)
			return ch - 48;
		if(ch == '*')
			return 14;
		if(ch == '#')
			return 15;
		if(ch == ' ')
			return 16;
		return -1;
	}
	
	char enc(int i) {
		if(10 <= i && i <= 13)
			return (char)(i + 55);
		
		if(0 <= i && i <= 9)
			return (char)(i + 48);
		
		switch(i) {	
			case 14:
				return '*';
			case 15:
				return '#';
			default:
				return ' ';
		}
	}
	
	char max(int[] chCounter) {
		// don't care bout ' ' (16th)
		int maxIdx = 0;
		for(int i=1; i<chCounter.length-1; i++) {
			if(chCounter[maxIdx] <= chCounter[i]) {
				maxIdx = i;
			}
		}
		if(chCounter[maxIdx] == 0)
			return ' ';
		return enc(maxIdx);
	}
	
	@Override
	public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
		handlePitchInternal(pitchDetectionResult, audioEvent);
	}
	
	char handlePitchInternal(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent)  {
		if(pitchDetectionResult.getPitch() == -1)
			return ' ';
	
		double timeStamp = audioEvent.getTimeStamp();
		float pitch = pitchDetectionResult.getPitch();
		float probability = pitchDetectionResult.getProbability();
		double rms = audioEvent.getRMS() * 100;
		
		if(pitch < 150 || pitch > 900 || rms < 10) {
			// disregard noise, no timestamping
			return ' ';
		}
		
		if(rawLog) {
			String message = String.format("Pitch detected at %.2fs: %.2fHz ( %.2f probability, RMS: %.5f )", 
					timeStamp, pitch, probability, rms);
			logViewer.log(message);
		}
		
		double timeGap = timeStamp - echoTimeStamp;
		echoTimeStamp = timeStamp;
		char ch = decodeDtmf(pitch);
		if(timeGap > commandWindow) {
			signalIdx = 0; // new signal starts.
			Arrays.fill(signal, ' ');
			signal[0] = ch;
			Arrays.fill(chCounter, 0);
			chCounter[dec(ch)] += 1;
			
			logViewer.log(String.format("[%d] = '%c' Pitch detected at %.2fs: %.2fHz", 
						signalIdx,signal[signalIdx%signal.length], timeStamp, pitch));
		} else if(timeGap > signalWindow) {
			signalIdx++;
			signal[signalIdx%signal.length] = ch;
			Arrays.fill(chCounter, 0);
			chCounter[dec(ch)] += 1;
			
			logViewer.log(String.format("[%d] = '%c' Pitch detected at %.2fs: %.2fHz", 
						signalIdx,signal[signalIdx%signal.length], timeStamp, pitch));
			
			// Full Signal!
			if(signal.length - 1 == signalIdx) {
				logViewer.log(String.format("#(%tc) COMMAND = [%c|%c|%c|%c]", 
						new Date(), signal[0], signal[1], signal[2], signal[3]));
				processCommand();
			}
		} else {
			// how frequent the pitch is.
			// most frequent one is the one.
			// for tie, larger char stands.
			chCounter[dec(ch)] += 1;
			char chMax = max(chCounter);
			if(chMax != ch) {
				signal[signalIdx%signal.length] = chMax;
				logViewer.log(String.format("[%d] = '%c' corrected from '%c' at %.2fs", 
						signalIdx, chMax, ch, timeStamp));
			}
		}
		return signal[signalIdx%signal.length];
	}
	
	private void processCommand() {
		if(Arrays.equals(ADS_START, signal)) {
			try {
				String cuepointId = adsManager.insertAds();
				logViewer.log(String.format("#(%tc) %s", new Date(), cuepointId));
			} catch (Exception e) {
				logViewer.log(e.getMessage());
			}
		}
	}
	
	float N1_2 = 172;
	float N1_1 = 239; 
	float N1 = 621, N2 = 674, N3 = 730, A = 798;
	float N4 = 397, N5 = 691, N6 = 745, B = 808;
	float N7 = 410, N8 = 439, N9 = 763, C = 824;
	float Na = 306, N0 = 453, Ns = 486, D = 844;

	// 1:621~7, 239~7, 172~7 
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