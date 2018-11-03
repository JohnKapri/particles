package net.johnkapri.particles;

import org.lwjgl.util.vector.Matrix4f;

/**
 * A layer of abstraction to unify the handling of screen contents to be
 * displayed.
 * 
 * @author John Kapri
 *
 */
public abstract class Screen {

	protected int width, height;
	protected Game game;

	/**
	 * Creates a new Screen with the given dimensions
	 * 
	 * @param game
	 *            Reference to the Game object that (indirectly) created this
	 *            Screen
	 * @param width
	 *            The width of the Screen
	 * @param height
	 *            The height of the Screen
	 */
	public Screen(Game game, int width, int height) {
		this.game = game;
		this.width = width;
		this.height = height;
	}
	
	public abstract void init();

	/**
	 * Updates the Screen's logic
	 * 
	 * @param delta
	 *            Time elapsed since the last update
	 */
	public abstract void tick(float delta);

	/**
	 * Draws the contents of this Screen to the framebuffer
	 */
	public abstract void render(Matrix4f projection, Matrix4f view);
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}
