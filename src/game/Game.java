package game;

import com.ardor3d.example.Exit;
import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.image.util.AWTImageLoader;

// SimpleUI
// Switch Node Example
// Shapes Example
public class Game implements Runnable, Exit
{
	private FrameHandler frameHandler;
	private final int WIDTH = 480;
	private DisplaySettings settings = new DisplaySettings((int) (WIDTH * 1.6), WIDTH, 24, 0, 0, 8, 0, 0, false, false);
	
	// Game states
	private GameState titleState;
	private GameState singlePlayerState;
	private GameState currentGameState;

	public static void main(final String[] args)
	{
		Game game = new Game();

		game.start();
	}

	private void start()
	{
		run();
	}

	public Game()
	{
		AWTImageLoader.registerLoader();
		
		//titleState = new TitleState(settings);
		singlePlayerState = new SinglePlayerGameState(settings);
		
		frameHandler = new FrameHandler(GameUtil.timer);
		frameHandler.init();
		
		setCurrentGameState(singlePlayerState);
	}

	public void run()
	{
		while (!getCurrentGameState().finished())
		{
			frameHandler.updateFrame();

			Thread.yield();
		}

		getCurrentGameState().endState();
		exit();
	}
	
	/**
	 * Removes old game state (if there is one) and adds the new game state
	 * @param gameState
	 */
	private void setCurrentGameState(GameState gameState)
	{
		if ( currentGameState != null )
		{
			frameHandler.removeCanvas(currentGameState.getCanvas());
			frameHandler.removeUpdater(currentGameState);
		}
		
		currentGameState = gameState;
		
		frameHandler.addCanvas(currentGameState.getCanvas());
		frameHandler.addUpdater(currentGameState);
	}
	
	private GameState getCurrentGameState()
	{
		return currentGameState;
	}

	@Override
	public void exit()
	{
		System.exit(0);		
	}
}
