package midi;

import javax.sound.midi.*;

public class MidiHelper
{
	private static final String[] NOTES = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };

	public static String getNote(int data)
	{
		return NOTES[data % 12];
	}

	public static int getOctave(int data)
	{
		return (data / 12);
	}

	public static float getBPM(MetaMessage message)
	{
		byte[]	data = message.getData();
		
		// tempo in microseconds per beat
		int	nTempo = ((data[0] & 0xFF) << 16) | ((data[1] & 0xFF) << 8) | (data[2] & 0xFF);

		// convert from microseconds per quarter note to beats per minute and vice versa
		float bpm = 60000000.0f / nTempo;
		
		return bpm;
	}
}
