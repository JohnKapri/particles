package net.johnkapri.particles.entity;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import net.johnkapri.particles.world.World;

public abstract class Entity {

	protected World world;
	public Vector3f pos;
	protected Vector3f rot;
	protected Vector3f vel;
	protected Vector3f acc;
	protected float friction = 10f;
	
	public Entity(World world) {
		this.world = world;
		pos = new Vector3f();
		vel = new Vector3f();
		acc = new Vector3f();
		rot = new Vector3f(0, 0, 0);
	}
	
	public abstract void init();
	
	public abstract void tick(float delta);
	
	protected Matrix4f getModelMatrix() {
		Matrix4f m = new Matrix4f();
		m.setIdentity();
		m.rotate(rot.x, new Vector3f(1, 0, 0));
		m.rotate(rot.y, new Vector3f(0, 1, 0));
		m.rotate(rot.z, new Vector3f(0, 0, 1));
		m.translate(pos);
		
		return m;
	}
	
	protected void movement(float delta) {
		vel.x += acc.x * delta / 80f;
		vel.y += acc.y * delta / 80f;
		vel.z += acc.z * delta / 80f;
		//vel.scale(1 / (friction * (delta / 80f)));
//		Vector2f frc = new Vector2f();
//		frc.set(vel);
//		frc.scale(1 / frc.length());
//		frc.negate();
//		vel.x -= (vel.x / friction) * (delta / 80f);
//		vel.y -= (vel.y / friction) * (delta / 80f);
		pos.x += vel.x * delta / 80f;
		pos.y += vel.y * delta / 80f;
		pos.z += vel.z * delta / 80f;
	}
	
	public abstract void render(Matrix4f projection, Matrix4f view);
}
