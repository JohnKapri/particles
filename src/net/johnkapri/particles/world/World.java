package net.johnkapri.particles.world;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import net.johnkapri.particles.Game;
import net.johnkapri.particles.Screen;
import net.johnkapri.particles.Shader;
import net.johnkapri.particles.entity.Entity;
import net.johnkapri.particles.entity.EntityParticleSystem;
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
 * The virtual environment of the game. Handles drawing and scrolling the
 * background layers as well as updating all the entities.
 * 
 * @author matthias
 *
 */
public class World extends Screen {

	private List<Entity> entities = new CopyOnWriteArrayList<Entity>();

	private static int TERRAIN_SHADER;

	private int terrainVBO;
	private int terrainArray;

	/**
	 * Creates a new World with the given dimensions
	 * 
	 * @param game
	 *            Reference to the Game object
	 * @param width
	 *            The width of the screen
	 * @param height
	 *            The height of the screen
	 */
	public World(Game game, int width, int height) {
		super(game, width, height);

		// addEntity(new EntityParticleSystem(this).setAngle(60f).setParticleAmmount(3000));
		addEntity(new EntityParticleSystem(this, new Vector3f(0f, 1f, 5f),
				new Vector3f(0f, 4f, 0f)).setLifeTime(4)
				.setParticleAmmount(100000).setGravity(new Vector3f(0, -2f, 0))
				.setParticleSize(30f).setParticleSpeed(0.01f));
	}

	@Override
	public void init() {
		float size = 10f;
		FloatBuffer buf = BufferUtils.createFloatBuffer(3 * 4);
		new Vector3f(-size, 0, -size).store(buf);
		new Vector3f(size, 0, -size).store(buf);
		new Vector3f(size, 0, size).store(buf);
		new Vector3f(-size, 0, size).store(buf);
		buf.flip();

		int vs = Shader.loadShader(GL_VERTEX_SHADER, "shader/terrain.vsh");
		int fs = Shader.loadShader(GL_FRAGMENT_SHADER, "shader/terrain.fsh");
		TERRAIN_SHADER = glCreateProgram();
		glAttachShader(TERRAIN_SHADER, vs);
		glAttachShader(TERRAIN_SHADER, fs);
		glLinkProgram(TERRAIN_SHADER);
		glValidateProgram(TERRAIN_SHADER);
		glDeleteShader(vs);
		glDeleteShader(fs);
		glUseProgram(TERRAIN_SHADER);

		terrainArray = glGenVertexArrays();
		glBindVertexArray(terrainArray);

		terrainVBO = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, terrainVBO);
		glBufferData(GL_ARRAY_BUFFER, buf, GL_STATIC_DRAW);

		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(0);
		glBindVertexArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		for (Entity e : entities) {
			e.init();
		}
	}

	@Override
	public void tick(float delta) {
		for (Entity e : entities) {
			e.tick(delta);
		}
	}

	@Override
	public void render(Matrix4f projection, Matrix4f view) {
		glUseProgram(TERRAIN_SHADER);
		Shader.loadMatrix4f(0, projection);
		Shader.loadMatrix4f(1, view);

		// glDisable(GL_CULL_FACE);
		glBindVertexArray(terrainArray);
		glDrawArrays(GL_LINE_LOOP, 0, 4);
		// glEnable(GL_CULL_FACE);

		for (Entity e : entities) {
			e.render(projection, view);
		}
	}

	/**
	 * Adds an Entity to the World. If the Entity already is present in this
	 * World, the call is neglected and the return statement is false.
	 * 
	 * @param e
	 *            Entity to be added
	 * @return Ture if the entity was added to this World, false otherwise
	 */
	public boolean addEntity(Entity e) {
		if (entities.contains(e)) {
			return false;
		}
		entities.add(e);
		return true;
	}

	/**
	 * Removes an Entity form this World (i.e. destroys it)
	 * 
	 * @param e
	 *            Entity to be removed
	 * @return True if the Entity existed and got removed, false otherwise
	 */
	public boolean removeEntity(Entity e) {
		if (!entities.contains(e)) {
			return false;
		}
		entities.remove(e);
		return true;
	}
}
