#version 330

in vec2 texCoord0;

out vec4 fragColor;

uniform vec3 R_ambient;
uniform sampler2D diffuse;

void main() {
    fragColor = texture(diffuse, texCoord0) * vec4(R_ambient, 1.0);
}