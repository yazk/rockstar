package guitar;
public class SoundHelper
{
	///////////////////////////////////////////////////////////////////////////
	//  Frequency           Tone Index
	//1st String = 329.627556912871	55
	//2nd String = 246.941650628063	50
	//3rd String = 195.997717990875	46
	//4th String = 146.832383958704	41
	//5th String = 110.0		        36
	//6th String = 82.4068892282176	31
	///////////////////////////////////////////////////////////////////////////
	private static String[] notes = { "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#" }; // 12 notes    
    private final static double[] STRINGFREQS = {329.627556912871,246.941650628063,195.997717990875,146.832383958704,110.0,82.4068892282176};
    
    /***
     * n % 12 will get the note
     */
    private static int getN(double frequency)
    {
    	// starts with frequency = 27.5hz (n=0)
    	
        int n = (int) Math.abs( Math.round((12.0 * (Math.log(frequency/440.0)/Math.log(2.0)) + 4 * 12.0)) );
        
        return n;
    }
    
    public static String getNote(double frequency)
    {
        if (frequency == 0)
            return "?";

        int n = getN(frequency);
        
        int r = n % 12; // figure out which note in scale

        return notes[r];
    }

    public static String getNote(int guitarString, int fret)
    {
        int n = getN(STRINGFREQS[guitarString-1])	+	fret;
        
        return notes[n%12];
    }

    public static double getFrequency(int guitarString, int fret)
    {
    	return STRINGFREQS[guitarString-1] + fret * Math.pow(2, 1 / 12.0);
    }

    public static double calcError(double actual, double theoretical)
    {
        return Math.abs(actual - theoretical) / theoretical * 100.0;
    }
    
    public static void main(String ... args)
    {
    	System.out.println("27.5hz= " + getNote(27.5) + " note");
    	System.out.println("440hz= " + getNote(440) + " note");
    	System.out.println("466.164hz= " + getNote(466.164) + " note");
    	System.out.println("493.883hz= " + getNote(493.883) + " note");
    	
    	System.out.println("1st String 0th fret= " + getNote(1,0));
    }
}