#version 440 core

layout (location=0) uniform mat4 projection;
layout (location=1) uniform mat4 view;
layout (location=2) uniform mat4 model;

layout (location=10) uniform float time;
layout (location=11) uniform vec3 gravity = vec3(0, 1.0, 0);
layout (location=12) uniform float lifeTime;
layout (location=13) uniform float size;

layout (location=0) in vec4 particle;

out float decay;

void main() {
	
    float t = time - particle.w;
    t = mod(t, lifeTime);

    vec4 p = vec4(particle.xyz * t + 0.5 * gravity * t * t, 1.0);
	
    decay = t / lifeTime;
	
	gl_Position = projection * view * model * p;
	gl_PointSize = size / length(inverse(view)[3].xyz - (inverse(model) * p).xyz);
	// gl_PointSize = 20f - 5f * decay;
}