#version 440 core

layout (location=0) uniform mat4 projection;
layout (location=1) uniform mat4 view;

layout (location=0) in vec3 pos;

void main() {
	gl_Position = projection * view * vec4(pos, 1.0);
}