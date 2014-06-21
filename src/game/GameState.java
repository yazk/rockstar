package game;

import java.net.URISyntaxException;

import com.ardor3d.framework.Canvas;
import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.framework.Scene;
import com.ardor3d.framework.Updater;
import com.ardor3d.framework.lwjgl.LwjglCanvas;
import com.ardor3d.framework.lwjgl.LwjglCanvasRenderer;
import com.ardor3d.input.Key;
import com.ardor3d.input.MouseButton;
import com.ardor3d.input.PhysicalLayer;
import com.ardor3d.input.control.FirstPersonControl;
import com.ardor3d.input.logical.AnyKeyCondition;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.input.logical.MouseButtonClickedCondition;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.input.lwjgl.LwjglKeyboardWrapper;
import com.ardor3d.input.lwjgl.LwjglMouseWrapper;
import com.ardor3d.intersection.PickData;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.intersection.PickingUtil;
import com.ardor3d.intersection.PrimitivePickResults;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector2;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.ui.text.BasicText;
import com.ardor3d.util.ReadOnlyTimer;
import com.ardor3d.util.Timer;
import com.ardor3d.util.resource.ResourceLocatorTool;
import com.ardor3d.util.resource.SimpleResourceLocator;

public abstract class GameState implements Scene, Updater
{
	private BasicText fpsText;

	// Root Node
	protected Node root;

	// Canvas
	protected LwjglCanvas canvas;
	protected DisplaySettings settings;
	protected Timer timer;
	protected String canvasTitle;

	// FPS Related
	double counter = 0;
	int frames = 0;
	
	// Z Buffer
	ZBufferState zBufferState;
	
    protected LogicalLayer logicalLayer;
    protected PhysicalLayer physicalLayer;

    private boolean isFinished;

	private FirstPersonControl controlHandle;
	protected Vector3 worldUp = new Vector3(0, 1, 0);

	@Override
	public void init()
	{		
		isFinished = false;
		
		// Canvas Setup ///////////////////////////////////////////////////////
		LwjglCanvasRenderer canvasRenderer = new LwjglCanvasRenderer(this);

		canvas = new LwjglCanvas(canvasRenderer, settings);
		canvas.setTitle(canvasTitle);
		canvas.setVSyncEnabled(true);
		canvas.init();
		
		// Layer setup
		physicalLayer = new PhysicalLayer(new LwjglKeyboardWrapper(), new LwjglMouseWrapper(), (LwjglCanvas)canvas);
        logicalLayer = new LogicalLayer();
		logicalLayer.registerInput(canvas, physicalLayer);
		registerInputTriggers();
		
		// Resource Setup ////////////////////////////////////////////////////////
		try
		{
			SimpleResourceLocator srl = new SimpleResourceLocator(SinglePlayerGameState.class.getClassLoader().getResource("game/"));
			ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, srl);
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}

		// Scene Setup ////////////////////////////////////////////////////////
		root = new Node();
		timer = GameUtil.timer;

		// FPS Text Setup
		fpsText = BasicText.createDefaultTextLabel("fpsText", "FPS");
		fpsText.setTextColor(ColorRGBA.DARK_GRAY);
		root.attachChild(fpsText);

		// Z Buffer
		zBufferState = new ZBufferState();
		zBufferState.setEnabled(true);
		zBufferState.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
		root.setRenderState(zBufferState);
	}

	@Override
	public void update(ReadOnlyTimer timer)
	{
		calculateFPS(timer);
		
		logicalLayer.checkTriggers(timer.getTimePerFrame());

		physicalLayer.drainAvailableStates();
		
		// Update controllers/render states/transforms/bounds for rootNode.
		root.updateGeometricState(timer.getTimePerFrame(), true);
	}

	@Override
    public PickResults doPick(final Ray3 pickRay)
	{
        final PrimitivePickResults pickResults = new PrimitivePickResults();
        
        pickResults.setCheckDistance(true);
        PickingUtil.findPick(root, pickRay, pickResults);
        processPicks(pickResults);
        
        return pickResults;
    }

	protected void processPicks(final PrimitivePickResults pickResults)
	{
		int index = findPick(pickResults);
		
		if ( index != -1 )
		{
			final PickData pick = pickResults.getPickData(index);
			System.out.println("picked: " + pick.getTargetMesh() + " at: " + pick.getIntersectionRecord().getIntersectionPoint(0));
		}
		else
		{
			System.out.println("picked: nothing");
		}
	}
	
	protected int findPick(final PrimitivePickResults pickResults)
	{
		for ( int i = 0; i < pickResults.getNumber(); ++i )
		{
			if ( pickResults.getPickData(i).getIntersectionRecord().getNumberOfIntersection() > 0 )
				return i;
		}
		
		return -1;
	}

	@Override
	public boolean renderUnto(final Renderer renderer)
	{
		renderer.draw(root);
		
		return true;
	}
	
	protected void registerInputTriggers()
	{
		controlHandle = FirstPersonControl.setupTriggers(logicalLayer, worldUp, true);
		
		logicalLayer.registerTrigger(new InputTrigger(new MouseButtonClickedCondition(MouseButton.LEFT), 
				new TriggerAction()
		{
			public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf)
			{
				//System.out.println("Left click");
				System.out.println("clicked: " + inputStates.getCurrent().getMouseState().getClickCounts());

				final Vector2 pos =
					Vector2.fetchTempInstance().set( inputStates.getCurrent().getMouseState().getX(), inputStates.getCurrent().getMouseState().getY());
				final Ray3 pickRay = new Ray3();
				canvas.getCanvasRenderer().getCamera().getPickRay(pos, false, pickRay);
				Vector2.releaseTempInstance(pos);
				doPick(pickRay);
			}
		}
		));
        
        logicalLayer.registerTrigger(new InputTrigger(new AnyKeyCondition(), new TriggerAction()
        {
            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf)
            {
                System.out.println("Key character pressed: " + inputState.getCurrent().getKeyboardState().getKeyEvent().getKeyChar());
                System.out.flush();
                
                Key key = inputState.getCurrent().getKeyboardState().getKeyEvent().getKey();
                
                if ( key == Key.ESCAPE )
                {
                	isFinished = true;
                }
            }
        }));
	}

	public void calculateFPS(final ReadOnlyTimer timer)
	{
		counter += timer.getTimePerFrame();
		frames++;

		if (counter > 1)
		{
			final double fps = (frames / counter);
			counter = 0;
			frames = 0;

			//System.out.printf("%7.1f FPS\n", fps);
			fpsText.setText(Integer.toString((int)(fps+0.5)));
		}
	}
	
	public LwjglCanvas getCanvas()
	{
		return canvas;
	}

	public boolean finished()
	{
		return canvas.isClosing() || isFinished;
	}

	public void endState()
	{
		canvas.close();
	}
}
