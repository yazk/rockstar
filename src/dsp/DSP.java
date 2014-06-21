package dsp;
import java.util.Random;
import javax.sound.sampled.AudioFormat;


import audio.AudioBuffer;

public class DSP
{
	private static Random rand = new Random();
	private static double MAX = Math.pow(2.0, 15.0) - 1;
	
	public static void reverse(AudioBuffer in)
	{
		int samples = in.getSampleCount();
		AudioBuffer out = new AudioBuffer(in.getSampleCount(), in.getAudioFormat());
		
		for ( int i = 0; i < samples; ++i )
		{
			out.setSample(i, in.getSample(samples - i - 1));
		}
	}
	
	public static int generateSinusoid(AudioFormat audioFormat, AudioBuffer audioBuffer, double frequency)
	{
		int samples = audioBuffer.getSampleCount();
		int sampleRate = (int) audioBuffer.getSampleRate();
		
		for ( int i = 0; i < samples; ++i )
		{
		    int wave = (int)(MAX * Math.sin(2.0 * Math.PI * frequency * i / sampleRate));
		    
		    // TODO: remove
		    wave = addNoise(wave);
		    
		    //wave += (int)(amplitude/2 * Math.sin(2.0 * Math.PI * (2 * frequency) * i / sampleRate));
		    
		    audioBuffer.setSample(i, wave);
		 }
		return audioBuffer.getBufferSize();
	}
	
	private static int addNoise(int signal)
	{
		int randValue = rand.nextInt((int) (MAX/4));
		
	    return ((signal + randValue) > MAX ? (int) MAX : signal + randValue);
	}
	
	public static void hardClip(byte[] audioBuffer, int u, int l)
	{
		byte upper = (byte)u;
		byte lower = (byte)l;
		
		for ( int i = 0; i < audioBuffer.length; ++i )
		{
			if ( audioBuffer[i] > upper )
				audioBuffer[i] = upper;
			else if ( audioBuffer[i] < lower )
				audioBuffer[i] = lower;
		}
	}
	
	public static void softClip(byte[] audioBuffer, int u)
	{
		byte upper = (byte)u;
		
		for ( int i = 0; i < audioBuffer.length; ++i )
		{
			if ( audioBuffer[i] > upper )
				audioBuffer[i] = (byte) (Math.log10( audioBuffer[i] / upper) * upper);
		}
	}
	
	public static float getDecible(Complex c)
	{
		return 10F * 0.5F * (float)Math.log10( c.abs() );
	}
	
	public static void scale(int[] buffer, float scale)
	{
		for ( int i = 0; i < buffer.length; ++i )
		{
			buffer[i] *= scale;
		}
	}
	
	public static void shift(int[] buffer, int shift)
	{
		for ( int i = 0; i < buffer.length; ++i )
		{
			buffer[i] += shift;
		}
	}
	
	// TODO: wtf is magic number n/32
	// TODO: also end of signal gets corrupted
	public static void averageFilter(int[] bufferAverage, int n)
	{		
		int[] bufferOriginal = bufferAverage.clone();
		
		bufferAverage[0] = bufferOriginal[0];
		
		float alpha = ((float)n) / (n+1);
		
		for ( int i = 1; i <  bufferAverage.length; ++i )
		{
			bufferAverage[i] = (int) (alpha * bufferAverage[i-1] + (1-alpha) * bufferOriginal[i]);
		}
		
	}
	
	public static void normalize(int[] buffer)
	{
		float scale;
		int max = Math.abs(buffer[0]);
		
		for ( int sample : buffer )
			if ( Math.abs(sample) > max )
				max = Math.abs(sample);
		
		scale = (float) (MAX / max);
		
		System.out.println("scale= " + scale);
		
		scale(buffer, scale);
	}
}
