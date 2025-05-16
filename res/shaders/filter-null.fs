#version 330

in vec2 texCoord0;

out vec4 fragColor;

uniform sampler2D R_filterTexture;

void main() {
    fragColor = texture(R_filterTexture, texCoord0);
}