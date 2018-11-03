package net.johnkapri.particles;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;

public class Texture {
	
	public static int loadTexture(String path) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int[] data = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
		int[] alpha = new int[img.getWidth() * img.getHeight()];
		img.getAlphaRaster().getPixels(0, 0, img.getWidth(), img.getHeight(), alpha);
		FloatBuffer imgBuff = BufferUtils.createFloatBuffer(img.getWidth() * img.getHeight() * 4);
		for (int n = 0; n < data.length; n++) {
			int i = data[n];
			imgBuff.put(((i >> 16) & 0x000000FF) / 256.0f);
			imgBuff.put(((i >> 8 ) & 0x000000FF) / 256.0f);
			imgBuff.put(((i) & 0x000000FF) / 256.0f);
			imgBuff.put(alpha[n] / 256.0f);
		}
		imgBuff.flip();
		
		int tex = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, tex);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri (GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri (GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri (GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, img.getWidth(), img.getHeight(), 0, GL_RGBA, GL_FLOAT, imgBuff);
		glBindTexture(GL_TEXTURE_2D, 0);
		
		imgBuff.clear();
		imgBuff = null;
		img.flush();
		img = null;
		
		return tex;
	}
	
	public static void bindTexture(int tex) {
		glBindTexture(GL_TEXTURE_2D, tex);
	}
}
