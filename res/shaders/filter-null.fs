#version 410 core

in vec2 texCoord0;

layout (location=0) out vec4 fragColor;

uniform sampler2D R_filterTexture;

void main() {
    fragColor = texture(R_filterTexture, texCoord0);
}