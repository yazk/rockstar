package midi;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.sound.midi.*;

public class Song implements Iterable<ArrayList<MidiEvent>>
{
	Sequence sequence;
	Track[] tracks;
	Patch[] patches;
	public Song(String fileName) throws Exception
	{
		sequence = MidiSystem.getSequence(new File(fileName));
		
		System.out.println("Sequence: " + sequence);
		System.out.println("Divison Type: " + (sequence.getDivisionType() == Sequence.PPQ ? "PPQ (ticks/beat)" : "SMTPE (ticks/frame)"));
		System.out.println("Sequence Duration (microseconds): " + sequence.getMicrosecondLength());
		System.out.println("Sequence Duration (seconds): " + sequence.getMicrosecondLength() / Math.pow(10,6));
		System.out.println("Sequence Duration (ticks): " + sequence.getTickLength());
		System.out.println("Resolution: " + sequence.getResolution());
		System.out.println("Tracks: " + sequence.getTracks().length);
		System.out.println("Patches: " + sequence.getPatchList().length);
		System.out.println();

		if ( sequence.getDivisionType() != Sequence.PPQ )
			throw new Exception("Only sequence division type PPQ is supported");
		
		
		tracks = sequence.getTracks();
		
		patches = sequence.getPatchList();
		
		for ( Patch patch : patches )
			System.out.println("Patch's bank= " + patch.getBank() + " Patch's program= " + patch.getProgram());
	}
	
	public Track getTrack(int i)
	{
		return tracks[i];
	}
	
	public int getNumTrack()
	{
		return tracks.length;
	}

	public Iterator<ArrayList<MidiEvent>> iterator()
	{
		return new Iterator<ArrayList<MidiEvent>>()
		{
			long currentTick = -1;
						
			@Override
			public boolean hasNext()
			{
				return currentTick != Long.MAX_VALUE ? true : false;
			}

			@Override
			public ArrayList<MidiEvent> next()
			{
				currentTick = findEarliestTick(currentTick);
				
				return getEvents(currentTick);
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}
	
	public long findEarliestTick(long startTick)
	{
		long earliestTick = Long.MAX_VALUE;
		
		for ( int i = 0; i < tracks.length; ++i )
		{
			Track track = tracks[i];
			
			for ( int j = 0; j < track.size(); ++j )
			{
				MidiEvent event = track.get(j);
				
				long currentTick = event.getTick();
				
				// could be shorter if break occurs after we find earliest tick
				if ( currentTick < earliestTick && currentTick > startTick )
				{
					earliestTick = currentTick;
					
					//break;
				}
			}
		}
		
		return earliestTick;
	}
	
	public ArrayList<MidiEvent> getEvents(long tick)
	{
		ArrayList<MidiEvent> events = new ArrayList<MidiEvent>();
		
		for ( int i = 0; i < tracks.length; ++i )
		{
			Track track = tracks[i];
			
			for ( int j = 0; j < track.size(); ++j )
			{
				MidiEvent event = track.get(j);
				
				if ( event.getTick() == tick && processEvent(event))
				{
					events.add(event);
				}
			}
		}
		
		return events;
	}

	private boolean processEvent(MidiEvent event)
	{
		if ( event.getMessage() instanceof ShortMessage )
		{
			ShortMessage message = (ShortMessage) event.getMessage();
			
			if ( message.getCommand() == ShortMessage.NOTE_ON || message.getCommand() == ShortMessage.NOTE_OFF )
				return true;
		}
		
		return false;
	}
	
	
	public void processMessage(MidiMessage message)
	{
		if ( message instanceof MetaMessage )
		{
			processMessage((MetaMessage) message);
		}
		else if ( message instanceof ShortMessage )
		{
			processMessage((ShortMessage) message);
		}
		else if ( message instanceof SysexMessage )
		{
			processMessage((SysexMessage) message);
		}
	}

	public void processMessage(MetaMessage message)
	{
		byte[] data = message.getData();
		
		System.out.printf("MetaMessage  [%d] ", message.getType());
		switch (message.getType())
		{
		case 0x03:
			System.out.print("Title: " + new String(message.getData()) + " ");
			break;
		case 0x2F:
			System.out.print("End of track ");
			break;
		case 0x51:
			System.out.print(MidiHelper.getBPM(message) + " BPM ");
			break;
		case 0x58:
			
			int beatsPerBar = (data[0] & 0xFF);
			int beatUnit = 1 << (data[1] & 0xFF);
			int midiClocksPerMetronomeTick = (data[2] & 0xFF);
			
			System.out.printf( "Time Signature: %d/%d,", beatsPerBar, beatUnit );
			System.out.printf( "MIDI clocks per metronome tick: %d,", midiClocksPerMetronomeTick);
			System.out.printf( "1/32 per 24 MIDI clocks: %d", (data[3] & 0xFF) );
			break;
		default:
			break;
		}
		
		System.out.println();
	}
	
	public void processMessage(ShortMessage message)
	{
		ShortMessage shortMessage = (ShortMessage) message;
		System.out.print("ShortMessage [" + shortMessage.getCommand() +"] ");

		if ( shortMessage.getCommand() == ShortMessage.CONTROL_CHANGE )
		{
			System.out.print("Control Change ");
		}
		else if ( shortMessage.getCommand() == ShortMessage.PITCH_BEND )
		{
			System.out.print("Pitch Bend ");
		}
		else if ( shortMessage.getCommand() == ShortMessage.NOTE_ON )
		{
			System.out.printf("Note On %s (%d)\t",  MidiHelper.getNote(shortMessage.getData1()), MidiHelper.getOctave(shortMessage.getData2()) );
		}
		else if ( shortMessage.getCommand() == ShortMessage.NOTE_OFF )
		{
			System.out.printf("Note Off %s (%d)\t",  MidiHelper.getNote(shortMessage.getData1()), MidiHelper.getOctave(shortMessage.getData2()));
		}
		else
		{
			System.out.print("UNKNOWN ");
		}
		
		System.out.println();
	}
	
	public void processMessage(SysexMessage message)
	{
		System.out.print("sysexMessage ");
		System.out.println();
	}
	
	public void dumpSong()
	{
		for (int i = 0; i < this.getNumTrack(); ++i )
		{
			Track track = this.getTrack(i);

			System.out.println();
			System.out.println("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
			System.out.println("Track " + i);
			System.out.println("Track Events: " + track.size());
			System.out.println("Track Duration (Ticks): " + track.ticks());

			for ( int j = 0; j < track.size(); ++j )
			{
				MidiEvent event = track.get(j); 
				MidiMessage midiMessage = event.getMessage();
				byte[] message = midiMessage.getMessage();

				processMessage(midiMessage);
				
				System.out.print("Event " + (j+1) + ": Tick: " + event.getTick() + " / ");
				System.out.print("Message: (" + midiMessage.getLength() + " bytes) 0x" + Integer.toHexString(midiMessage.getStatus()) + " (" + midiMessage.getClass().getSimpleName() + ") ");
				for ( int k = 0; k < message.length; ++k)
				{
					System.out.print("0x" + Integer.toHexString(message[k]) + " ");
				}
				System.out.println();
			}
		}
	}
}
