package game;

import com.ardor3d.bounding.BoundingBox;
import com.ardor3d.image.Texture;
import com.ardor3d.image.Image.Format;
import com.ardor3d.math.Matrix4;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.Vector4;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.renderer.state.MaterialState.ColorMaterial;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.scenegraph.shape.Box;
import com.ardor3d.scenegraph.shape.Quad;
import com.ardor3d.util.TextureManager;

public class Guitar extends Spatial
{
	// Nodes
	private Node root;
	// Textures
	private Texture fretBoardTexture;
	private Texture noteMarkerTexture;
	
	// Texture states
	private TextureState fretBoardState;
	private TextureState noteMarkerState;
	
	private MaterialState materialState;

	// Meshes
	private Quad fretBoard;
	private Quad noteMarker;
	
	
	public Guitar()
	{
		root = new Node();
		
		// Texture setup //
		
		// fretBoard
		//fretBoard.png
		fretBoardTexture = TextureManager.load("fretBoard.png", Texture.MinificationFilter.Trilinear, Format.GuessNoCompression, true);
		//fretBoardTexture.setWrap(WrapAxis.S, WrapMode.Repeat);
		Matrix4 scaleMatrix = new Matrix4();
		scaleMatrix.scaleLocal(new Vector4(2,1,1,1));
		//fretBoardTexture.setTextureMatrix(scaleMatrix);
		
		// noteMarker noteMarker.png
		noteMarkerTexture = TextureManager.load("noteMarker.png", Texture.MinificationFilter.Trilinear, Format.GuessNoCompression, true);
		
		// State setup //
		fretBoardState = new TextureState();
		fretBoardState.setTexture(fretBoardTexture);
		
		noteMarkerState = new TextureState();
		noteMarkerState.setTexture(noteMarkerTexture);
		
		// State
		materialState = new MaterialState();
		materialState.setColorMaterial(ColorMaterial.Diffuse);
		
		// Spatial setup
//		box = new Box("myBox", new Vector3(), 0.5,0.5,0.5);
//		box.setModelBound(new BoundingBox());
//		box.setRenderState(materialState);
//		box.setRenderState(noteMarkerState);
//		//root.attachChild(box);
		
		fretBoard = new Quad("fretBoard", 327*2,225);
		fretBoard.setModelBound(new BoundingBox());
		fretBoard.setRenderState(materialState);
		fretBoard.setRenderState(fretBoardState);
		fretBoard.setTranslation(0, 110, 0);
		root.attachChild(fretBoard);
		
		noteMarker = new Quad("noteMarker0", 64,64);
		noteMarker.setModelBound(new BoundingBox());
		noteMarker.setRenderState(materialState);
		noteMarker.setRenderState(noteMarkerState);
		noteMarker.setTranslation(500,200,0);
		root.attachChild(noteMarker);
		
		// Setup orthographic projection
		root.getSceneHints().setRenderBucketType(RenderBucketType.Ortho);
		root.getSceneHints().setCullHint(CullHint.Never);
	}
	
	public Node getRoot()
	{
		return root;
	}

	@Override
	public void draw(Renderer renderer)
	{
		root.draw(renderer);
		
	}

	@Override
	public void updateWorldBound(boolean recurse)
	{
		root.updateWorldBound(recurse);
		
	}
}
