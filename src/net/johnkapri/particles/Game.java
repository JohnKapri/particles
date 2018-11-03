package net.johnkapri.particles;

import java.nio.FloatBuffer;

import net.johnkapri.particles.world.World;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.vector.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.opengl.GL42.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.opengl.GL44.*;

/**
 * Handles initialization of OpenGL, handles the logic updates (ticks) and
 * rendering.
 * 
 * @author John Kapri
 * 
 */
public class Game implements Runnable {

	public static String NAME = "Sidescroller";
	public static int WIDTH = 1024;
	public static int HEIGHT = 3 * WIDTH / 4;

	private Screen screen;
	private Camera camera;
	private long lastTick;
	private long lastInfo;
	private int fps;

	@Override
	public void run() {
		init();
		lastInfo = System.currentTimeMillis();
		while (!Display.isCloseRequested()) {
			float delta = getDelta();
			tick(delta);
			render();
			Display.update();
			Display.sync(60);
			
			fps++;
			if(lastInfo + 1000 <= System.currentTimeMillis()) {
				System.out.println("FPS:   " + fps);
				System.out.println("DELTA: " + delta);
				fps = 0;
				lastInfo = System.currentTimeMillis();
			}
		}
		quitGame();
	}

	/**
	 * Initializes OpenGL, creates the window, creates a World
	 */
	public void init() {
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			// Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
			Display.setTitle(NAME);
			Display.setResizable(false);
			Display.setVSyncEnabled(true);
			Display.setInitialBackground(0, 0, 0);
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		System.out.println("OpenGL version:  " + glGetString(GL_VERSION));
		glColor3f(1, 1, 1);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		glEnable(GL_POINT_SPRITE);
		glTexEnvi(GL_POINT_SPRITE, GL_COORD_REPLACE, GL_TRUE);
		glEnable(GL_PROGRAM_POINT_SIZE);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_BLEND);
		glEnable(GL_DEPTH);
		// glEnable(GL_DEPTH_TEST);
		
		camera = new EulerCamera((float) WIDTH / (float) HEIGHT, 0, 5, -2, 0, 90, 0, 0.001f, 1000f);
		camera.setFieldOfView(3.0f * 90.0f / 4.0f);
		screen = new World(this, Display.getWidth(), Display.getHeight());		
		screen.init();
		
		Mouse.setGrabbed(true);
		Mouse.setClipMouseCoordinatesToWindow(true);
	}

	/**
	 * Updates the game's logic
	 * 
	 * @param delta
	 *            The time passed since the last iteration
	 */
	private void tick(float delta) {
		camera.processKeyboard(delta);
		camera.processMouse();
				
		if (screen != null) {
			screen.tick(delta);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			quitGame();
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_TAB)) {
			if(Mouse.isGrabbed()) {
				Mouse.setClipMouseCoordinatesToWindow(false);
				Mouse.setGrabbed(false);
			} else {
				Mouse.setGrabbed(true);
				Mouse.setClipMouseCoordinatesToWindow(true);
			}
		}
	}

	/**
	 * Draws the game contents
	 */
	private void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		if (screen != null) {
			screen.render(camera.getProjectionMatrix(), camera.getViewMatrix());
		}
	}

	/**
	 * Destroys the OpenGL context and shuts the JVM down
	 */
	public void quitGame() {
		Display.destroy();
		System.exit(0);
	}

	/**
	 * Calculates the passed time since the method got calld last
	 * 
	 * @return The difference of time in milliseconds
	 */
	private float getDelta() {
		long currentTime = getTime();
		float delta = currentTime - lastTick;
		lastTick = getTime();
		return delta;
	}

	/**
	 * Returns a precise system time
	 * 
	 * @return The system time in milliseconds
	 */
	public static long getTime() {
		return (Sys.getTime() * 1000l) / Sys.getTimerResolution();
	}

	/**
	 * Entry hook for the JVM
	 * 
	 * @param args
	 *            Start parameters (from the command line)
	 */
	public static void main(String[] args) {
		Thread t = new Thread(new Game(), NAME + "_main");
		t.start();
	}
}
