package audio;

import java.awt.Color;
import java.io.*;
import java.util.ArrayList;
import javax.sound.sampled.*;

import visual.Chart;
import visual.SwingWindow;

import dsp.Complex;
import dsp.DSP;
import dsp.FFT;


public class MyAudioCapture
{
	private static AudioFormat getAudioFormat()
	{
		float sampleRate = 44100F;		// 8000,11025,16000,22050,44100
		int sampleSizeInBits = 16;		// 8,16
		int channels = 2;				// 1,2
		boolean signed = true;			// true,false
		boolean bigEndian = true;		// true,false

		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}
	
	public static void main( String ... args ) throws LineUnavailableException, IOException
	{
		// Other variables (Non-audio related)
		ArrayList<Chart> charts = new ArrayList<Chart>();
		SwingWindow window = new SwingWindow(charts);
		charts.add( new Chart(window.getWidth(),window.getHeight(), Color.BLUE) );
		charts.add( new Chart(window.getWidth(),window.getHeight(), Color.RED) );
		charts.add( new Chart(window.getWidth(),window.getHeight(), Color.BLACK) );
		
		// Audio Global Space
		AudioFormat audioFormat = getAudioFormat();
		AudioBuffer audioBuffer = new AudioBuffer(2 << 15, audioFormat);
		Complex[] complex;
		int cnt;
		
		// Recorder variables //
		DataLine.Info dataLineInfoTarget = new DataLine.Info(TargetDataLine.class, audioFormat);
		TargetDataLine targetDataLine;
		//
		
		// Player variables //
		DataLine.Info dataLineInfoSource = new DataLine.Info(SourceDataLine.class, audioFormat);
		SourceDataLine sourceDataLine;
		//
		
		targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfoTarget);
		targetDataLine.open(audioFormat);
		targetDataLine.start();
		
		sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfoSource);
		sourceDataLine.open(audioFormat);
		sourceDataLine.start();
		
		while ( true )
		{
			//cnt = targetDataLine.read(audioBuffer, 0, audioBuffer.length);
			cnt = DSP.generateSinusoid(audioFormat, audioBuffer, 440);
			
			int[] signal = audioBuffer.toInteger();
			int[] signalOriginal = signal.clone();
			
			complex = FFT.fft(audioBuffer.toComplex());
			
			float freqRes = audioBuffer.getFrequencyResolution();
			float freq = 0;
			int dataLength = complex.length/2;
			float db;
			int[] dbList = new int[dataLength];
			int[] freqList = new int[dataLength];
			
			System.out.println("Frequency resolution= " + freqRes);
			
			if ( freqRes < 1 )
				System.out.println("Audio was over sampled by " + 1.0 / freqRes);
			
			for ( int i = 0; i < dataLength; ++i )
			{
				db = DSP.getDecible( complex[i] );
				 
				//if ( db > 30 )
				//	System.out.println((int)freq + "-" + (int)(freq+freqRes) + "hz " + (int)db + " db");
				
				freqList[i] = (int) freq;
				dbList[i] = (window.getHeight() - (int) (db * 20)) + 150;
				freq += freqRes;
			}
			
			DSP.averageFilter(signal, 16);
			DSP.normalize(signal);
			DSP.averageFilter(dbList, 2);
			
			audioBuffer.set(signal);
			
			DSP.scale(signal, 0.005f);
			DSP.shift(signal, 300);
			
			DSP.scale(signalOriginal, 0.005f);
			DSP.shift(signalOriginal, 300);
			
			charts.get(0).setYPoints(signalOriginal);
			charts.get(0).setPoints(signalOriginal.length);
			
			charts.get(1).setYPoints(dbList);
			charts.get(1).setPoints(dbList.length);
			
			charts.get(2).setYPoints(signal);
			charts.get(2).setPoints(signal.length);
			
			window.repaintPanel();
			
			//DSP.reverse(audioBuffer);

			sourceDataLine.write(audioBuffer.getBytes(), 0, cnt);
			
			break;
		}
	}
	
	public static String getBar(int barLength)
	{
		StringBuilder sb = new StringBuilder();
		
		while ( barLength > 0 )
		{
			sb.append("*");
			barLength--;
		}
		
		return sb.toString();
	}
}
