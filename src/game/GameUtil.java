package game;

import java.util.Random;

import com.ardor3d.light.Light;
import com.ardor3d.light.PointLight;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.util.ReadOnlyTimer;
import com.ardor3d.util.Timer;

public class GameUtil
{
	// Timer
	public static Timer timer = new Timer();
	
	private static Random rand = new Random();
	
	public static Light makeRandomLight()
	{
		final PointLight light = new PointLight();
		light.setDiffuse(new ColorRGBA(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 1f));
		light.setAmbient(new ColorRGBA(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 1f));
		light.setLocation(new Vector3(rand.nextInt(5), rand.nextInt(5), rand.nextInt(5)));
		light.setEnabled(true);

		return light;
	}

	public static Light makeBrightLight(int x, int y, int z)
	{
		final PointLight light = new PointLight();
		light.setDiffuse(new ColorRGBA(1,1,1, 1));
		light.setAmbient(new ColorRGBA(1,1,1, 1));
		light.setLocation(new Vector3(x,y,z));
		light.setEnabled(true);

		return light;
	}
}
