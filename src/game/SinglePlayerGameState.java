package game;

import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Plane;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.scenegraph.shape.Box;
import com.ardor3d.util.ReadOnlyTimer;

public class SinglePlayerGameState extends GameState
{
	private Box box;
	
	// States
	private LightState lightState;
	private Guitar guitar;
	private Camera cam2;
	
	// Rotation Related
	private final Vector3 axis = new Vector3(1, 0, 1).normalizeLocal();
	private final Matrix3 rotate = new Matrix3();
	private double angle = 0;
	
	public SinglePlayerGameState(DisplaySettings settings)
	{
		this.settings = settings;
		this.canvasTitle = "Single Player State";
		
		init();
	}

	@Override
	public void init()
	{
		super.init();
		
		// Scene Setup ////////////////////////////////////////////////////////
		// State setup
		// Light state Setup //
		lightState = new LightState();
		lightState.attach(GameUtil.makeBrightLight(0, 0, 10));
		lightState.setNeedsRefresh(true);
		lightState.setEnabled(true);

		// Add guitar model
		guitar = new Guitar();
		root.attachChild(guitar.getRoot());
		
//		cam2 = new Camera(500, 500);
//		cam2.set(canvas.getCanvasRenderer().getCamera());
//		cam2.setFrustumPerspective(45, 1.6, 1, 1000);
//		cam2.setParallelProjection(false);
//		cam2.setViewPort(0, 1, 0, 1);

		box = new Box("mybox", new Vector3(), new Vector3(10,10,10));
		root.attachChild(box);
		
		root.setRenderState(lightState);
	}
	
	private void rotateBox()
	{
		// update our rotation
		angle += (timer.getTimePerFrame() * 25);
		
		if (angle > 180)
			angle = -180;

		rotate.fromAngleNormalAxis(angle * MathUtils.DEG_TO_RAD, axis);
		box.setRotation(rotate);	
	}
	
	@Override
	public void update(ReadOnlyTimer timer)
	{		
		rotateBox();
		
		Node guitarNode = guitar.getRoot();
		
		ReadOnlyVector3 translation = guitarNode.getTranslation();
		guitarNode.setTranslation(translation.getX()-1,translation.getY(),translation.getZ());
		
		super.update(timer);
	}
}
