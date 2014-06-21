package audio;

import java.util.Iterator;

import javax.sound.sampled.AudioFormat;

import dsp.Complex;

public class AudioBuffer implements Iterable<Integer>
{
	final byte[] buffer;
	final AudioFormat audioFormat;
	
	final float sampleRate;
	final int sampleSizeInBits;
	final int sampleSizeInBytes;
	final int channels;
	final boolean signed;
	final boolean bigEndian;
	
	final int sampleCount;
	
	public AudioBuffer(int samples, AudioFormat audioFormat)
	{
		if ( audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED && audioFormat.getEncoding() != AudioFormat.Encoding.PCM_UNSIGNED )
			throw new IllegalArgumentException("Non-PCM Encoding not supported");
		else if ( audioFormat.getSampleSizeInBits() % 8 != 0 )
			throw new IllegalArgumentException("Non-multiple byte for sample size");
		else if ( audioFormat.getSampleSizeInBits() > 16 )
			throw new IllegalArgumentException("AudioBuffer does not support sample sizes greater than 16-bit");
		else if ( ! audioFormat.isBigEndian() )
			throw new IllegalArgumentException("AudioBuffer supports only big-endian");
		
		this.audioFormat = audioFormat;
		
		this.sampleRate = audioFormat.getSampleRate();
		this.sampleSizeInBits = audioFormat.getSampleSizeInBits();
		this.sampleSizeInBytes = audioFormat.getSampleSizeInBits() / 8;
		this.channels = audioFormat.getChannels();
		this.signed = audioFormat.getEncoding() == AudioFormat.Encoding.PCM_SIGNED ? true : false;
		this.bigEndian = audioFormat.isBigEndian();
		
		this.sampleCount = samples;
		
		this.buffer = new byte[samples * channels * sampleSizeInBytes];
	}
	
	public byte[] getBytes()
	{
		return buffer;
	}
	
	public AudioFormat getAudioFormat()
	{
		return audioFormat;
	}
	
	public int getSampleCount()
	{
		 return sampleCount;
	}
	
	public void setSample(int sampleIndex, int value)
	{
		int j = sampleIndex * channels * sampleSizeInBytes;
		
		byte msb = (byte)(value >>> 8);
		byte lsb = (byte) value;
	    
	    buffer[j++] = msb;
	    if (sampleSizeInBytes > 1)
	    {
	        buffer[j++] = lsb;
	    }
	    if (channels > 1)
	    {
	        buffer[j++] = msb;
	        
	        if (sampleSizeInBytes > 1)
	        {
	            buffer[j++] = lsb;
	        }
	    }
	}
	
	public int getSample(int sampleIndex)
	{
		int bufferIndex = sampleIndex * channels * sampleSizeInBytes;
		
		int value = buffer[bufferIndex];
		
		if ( sampleSizeInBytes == 1 )
		{
			if ( channels == 1 )
				return value;
			else
				return (value + buffer[bufferIndex+1]) / 2;
		}
		
		else if ( sampleSizeInBytes == 2 )
		{
			value = (value << 8) | buffer[bufferIndex+1] & 0xFF;
			
			if ( channels == 1 )
			{
				return value;
			}
			
			else
			{
				bufferIndex += sampleSizeInBytes;
				
				int rightChannelValue = buffer[bufferIndex];
				
				rightChannelValue = (rightChannelValue << 8) | buffer[bufferIndex+1] & 0xFF;
				
				return (value + rightChannelValue) / 2;
			}
		}
		
		throw new IllegalStateException("getSample conditions not met");
	}
	
	public float getSampleRate()
	{
		return sampleRate;
	}
	
	public int getBufferSize()
	{
		return buffer.length;
	}
	
	public float getFrequencyResolution()
    {
        return sampleRate / sampleCount;
    }
	
	public Complex[] toComplex()
	{
		Complex[] complex = new Complex[sampleCount];
		
		for ( int i = 0; i < sampleCount; ++i )
			complex[i] = new Complex(getSample(i), 0);
		
		return complex;
	}
	
	public int[] toInteger()
	{
		int[] integers = new int[sampleCount];
		
		for ( int i = 0; i < sampleCount; ++i )
			integers[i] = getSample(i);
		
		return integers;
	}
	
	public void set(int[] buffer)
	{
		for ( int i = 0; i < buffer.length; ++i )
			setSample(i, buffer[i]);
	}

	@Override
	public Iterator<Integer> iterator()
	{
		return new Iterator<Integer>()
		{
			int index = 0;
			
			@Override
			public void remove()
			{
				index++;
			}
		
			@Override
			public Integer next()
			{
				return getSample(index);
			}
		
			@Override
			public boolean hasNext()
			{
				return (index < sampleCount ? true : false);
			}
		};
	}
}
