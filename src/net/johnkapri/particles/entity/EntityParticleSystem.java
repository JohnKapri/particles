package net.johnkapri.particles.entity;

import java.nio.FloatBuffer;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import net.johnkapri.particles.Shader;
import net.johnkapri.particles.Texture;
import net.johnkapri.particles.world.World;
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

public class EntityParticleSystem extends Entity {

	private static int PARTICLE_SHADER;
	
	private int particleAmmount;
	private float angle;
	private Vector3f velocity;
	private Vector3f gravity;
	private float lifeTime;
	private float particleSize;
	private float particleSpeed;
	
	private int positionVBO;
	private int particleVAO;
	private float time;
	private int particleTexture;
	
	public EntityParticleSystem(World world) {
		super(world);
		particleAmmount = 1000;
		angle = 60f;
		velocity = new Vector3f(0f, 0f, 0f);
		gravity = new Vector3f(0f, 0.5f, 0f);
		lifeTime = 5.0f;
		particleSize = 240f;
		particleSpeed = 0.01f;
	}
	
	public EntityParticleSystem(World world, Vector3f position) {
		this(world);
		this.pos.set(position);
	}
	
	public EntityParticleSystem(World world, Vector3f position, Vector3f paticleVelocity) {
		this(world, position);
		this.velocity.set(paticleVelocity);
	}
	
	public EntityParticleSystem setParticleAmmount(int ammount) {
		particleAmmount = ammount;
		return this;
	}
	
	public EntityParticleSystem setAngle(float emmitAngle) {
		angle = emmitAngle;
		return this;
	}
	
	public EntityParticleSystem setLifeTime(float time) {
		lifeTime = time;
		return this;
	}
	
	public EntityParticleSystem setGravity(Vector3f gravity) {
		this.gravity.set(gravity);
		return this;
	}
	
	public EntityParticleSystem setParticleSize(float size) {
		particleSize = size;
		return this;
	}
	
	public EntityParticleSystem setParticleSpeed(float speed) {
		particleSpeed = speed;
		return this;
	}

	@Override
	public void init() {
		// Generate initial conditions for particles
		Random r = new Random();
		FloatBuffer particles = BufferUtils.createFloatBuffer(particleAmmount * 4);
		for(int i = 0; i < particleAmmount; i++) {
			Vector3f dir = new Vector3f((r.nextFloat() - 0.5f) * angle / 180.0f, (r.nextFloat() - 0.5f) * angle / 180.0f, (r.nextFloat() - 0.5f) * angle / 180.0f);
			Vector3f.add(velocity, dir, dir);
			// dir.store(particles);
			particles.put(dir.x);
			particles.put(dir.y);
			particles.put(dir.z);
			particles.put(r.nextFloat() * lifeTime);
		}
		particles.flip();
		
		if(PARTICLE_SHADER == 0) {
			// Load shaders and create program
			int vs = Shader.loadShader(GL_VERTEX_SHADER, "shader/particle.vsh");
			int fs = Shader.loadShader(GL_FRAGMENT_SHADER, "shader/particle.fsh");
			PARTICLE_SHADER = glCreateProgram();
			glAttachShader(PARTICLE_SHADER, vs);
			glAttachShader(PARTICLE_SHADER, fs);
			glLinkProgram(PARTICLE_SHADER);
			glValidateProgram(PARTICLE_SHADER);
			glDeleteShader(vs);
			glDeleteShader(fs);
			glUseProgram(PARTICLE_SHADER);
		}
		
		// Generate vertex array
		particleVAO = glGenVertexArrays();
		glBindVertexArray(particleVAO);
		
		// Load particle buffer
		positionVBO = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, positionVBO);
		glBufferData(GL_ARRAY_BUFFER, particles, GL_STATIC_DRAW);
		
		// Setup vertex array
		glVertexAttribPointer(0, 4, GL_FLOAT, false, 0, 0);
		glEnableVertexAttribArray(0);
		glBindVertexArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		// Load Texture
		particleTexture = Texture.loadTexture("res/round.png");
	}
	
	private void setUniforms() {
		// Set time elapsed
		glUniform1f(10, time);
		
		// Set Gravity
		FloatBuffer buf = BufferUtils.createFloatBuffer(3);
		gravity.store(buf);
		buf.flip();
		glUniform3(11, buf);
		buf.clear();
		
		// Set lifeTime
		glUniform1f(12, lifeTime);
		
		// Set particle size
		glUniform1f(13, particleSize);
	}

	@Override
	public void tick(float delta) {
		// System.out.println(delta * 0.001f);
		time += particleSpeed;
	}

	@Override
	public void render(Matrix4f projection, Matrix4f view) {
		glUseProgram(PARTICLE_SHADER);
		Shader.loadMatrix4f(0, projection);
		Shader.loadMatrix4f(1, view);
		Shader.loadMatrix4f(2, getModelMatrix());
		setUniforms();
		
		Texture.bindTexture(particleTexture);
		
		glBindVertexArray(particleVAO);
		glDrawArrays(GL_POINTS, 0, particleAmmount);
	}
}
