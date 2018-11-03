package net.johnkapri.particles;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_SHADER_SOURCE_LENGTH;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;

public class Shader {

	public static int loadShader(int type, String path) {
		long start = System.currentTimeMillis();
		
		File f = new File(path);
		int shaderID;
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(f));
		} catch (FileNotFoundException e1) {
			System.err.println("Shader file \"" + f.getName() + "\" not found!");
		}
		StringBuilder source = new StringBuilder();
		String line;
		try {
			while ((line = br.readLine()) != null) {
				source.append(line).append("\n");
			}
			br.close();
		} catch (IOException e) {
			System.err.println("Error while reading shader file \"" + f.getName() + "\"!");
		}

		shaderID = glCreateShader(type);
		
		// Create and compile shader
		glShaderSource(shaderID, source);
		glCompileShader(shaderID);

		// Check if the shader was compiles successfully
		if ((glGetShaderi(shaderID, GL_COMPILE_STATUS)) == GL_FALSE) {
			System.err.println("Shader " + f.getName()
					+ " didn't compile!");
			System.err.println(glGetShaderInfoLog(shaderID, 1000));
			glDeleteShader(shaderID);
			shaderID = -1;
		} else {
			System.out.println("Loaded shader \'"
					+ f.getParentFile().getName() + File.separator
					+ f.getName() + "\'");
			System.out.println("   It took:       "
					+ (System.currentTimeMillis() - start) + "ms");
			System.out.println("   Source length: "
					+ glGetShaderi(shaderID, GL_SHADER_SOURCE_LENGTH));
		}
		
		return shaderID;
	}
	
	public static void attachShaderTo(int program, int type, String path) {
		int shader = loadShader(type, path);
		glAttachShader(program, shader);
		// glDeleteShader(shader);
	}
	
	public static void loadMatrix4f(int location, Matrix4f mat) {
		FloatBuffer buf = BufferUtils.createFloatBuffer(4 * 4);
		mat.store(buf);
		buf.flip();
		glUniformMatrix4(location, false, buf);
		buf.clear();
	}
}
