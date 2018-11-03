#version 440 core

uniform sampler2D s;

in float decay;

out vec4 color;

void main() {
	color = texture(s, gl_PointCoord) * vec4(0.6 + pow(1-decay, 3) * 0.4, 0.6 + pow(1-decay, 3) * 0.2, 0.6 + (1-decay) * 0., 1- pow(decay, 4));
}