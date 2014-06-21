package midi;

import java.util.ArrayList;

import javax.sound.midi.*;

public class MidiTest
{
	public static void main(String[] args) throws Exception
	{
		Song song = new Song("data/7tracks-allnotes.mid");		
		
		for (ArrayList<MidiEvent> events : song)
		{
			for (MidiEvent event : events)
			{
				MidiMessage message = event.getMessage();
				
			}
			
			System.out.println();
		}
	}
}
