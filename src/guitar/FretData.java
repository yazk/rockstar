package guitar;
class FretData
{
	// Create getters and setters rather than use public
    public String note;
    public double frequency;

    public FretData()
    {
        note = null;
        frequency = -1;
    }

    public FretData(String n, double f)
    {
        note = n;
        frequency = f;
    }
    
    public String ToString()
    {
        return note + " @ " + (float)frequency;
    }
}