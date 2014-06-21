package game;

import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.framework.Scene;
import com.ardor3d.framework.Updater;
import com.ardor3d.framework.lwjgl.LwjglCanvas;
import com.ardor3d.framework.lwjgl.LwjglCanvasRenderer;
import com.ardor3d.image.Texture;
import com.ardor3d.image.Image.Format;
import com.ardor3d.image.util.AWTImageLoader;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.renderer.state.MaterialState.ColorMaterial;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.shape.Box;
import com.ardor3d.scenegraph.shape.Quad;
import com.ardor3d.ui.text.BasicText;
import com.ardor3d.util.ReadOnlyTimer;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.Timer;

public class TitleState extends GameState
{	
	// Spatials
	private Quad titleQuad;
	BasicText fpsText;

	// Texture States
	private TextureState titleTextureState;
	private TextureState testTextureState;

	// Material States
	private MaterialState materialState;
	
	// Light States
	private LightState lightState;

	// Textures
	private Texture testTexture;
	private Texture titleTexture;

	public TitleState(DisplaySettings settings)
	{
		this.settings = settings;
		this.canvasTitle = "Title State";

		init();
	}

	@Override
	public void init()
	{
		super.init();
		
		// Scene Setup ////////////////////////////////////////////////////////
		
		// Texture setup //
		testTexture = TextureManager.load("img/wall.jpg", Texture.MinificationFilter.Trilinear, Format.GuessNoCompression, true);
		titleTexture = TextureManager.load("img/title.png", Texture.MinificationFilter.Trilinear, Format.GuessNoCompression, true);

		// State setup //
		testTextureState = new TextureState();
		testTextureState.setTexture(testTexture);
		
		titleTextureState = new TextureState();
		titleTextureState.setTexture(titleTexture);
		
		materialState = new MaterialState();
		materialState.setColorMaterial(ColorMaterial.Diffuse);

		// Light state Setup //
		lightState = new LightState();
		lightState.attach(GameUtil.makeRandomLight());
		lightState.attach(GameUtil.makeRandomLight());
		lightState.attach(GameUtil.makeRandomLight());
		lightState.setNeedsRefresh(true);
		lightState.setEnabled(true);

		// Spatial setup
		titleQuad = new Quad("title", 7, 2);
		titleQuad.setModelBound(new BoundingBox());
		titleQuad.setRenderState(materialState);
		titleQuad.setRenderState(titleTextureState);
		root.attachChild(titleQuad);

		root.setRenderState(lightState);
	}
}
