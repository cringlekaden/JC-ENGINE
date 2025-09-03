#version 410 core

in vec2 texCoord0;

layout (location=0) out vec4 fragColor;

uniform vec3 R_blurScale;
uniform sampler2D R_filterTexture;

void main() {
    vec4 color = vec4(0.0);
    color += texture(R_filterTexture, texCoord0 + (vec2(-3.0) * R_blurScale.xy)) * (1.0/64.0);
    color += texture(R_filterTexture, texCoord0 + (vec2(-2.0) * R_blurScale.xy)) * (6.0/64.0);
    color += texture(R_filterTexture, texCoord0 + (vec2(-1.0) * R_blurScale.xy)) * (15.0/64.0);
    color += texture(R_filterTexture, texCoord0 + (vec2(0.0) * R_blurScale.xy)) * (20.0/64.0);
    color += texture(R_filterTexture, texCoord0 + (vec2(1.0) * R_blurScale.xy)) * (15.0/64.0);
    color += texture(R_filterTexture, texCoord0 + (vec2(2.0) * R_blurScale.xy)) * (6.0/64.0);
    color += texture(R_filterTexture, texCoord0 + (vec2(3.0) * R_blurScale.xy)) * (1.0/64.0);
    fragColor = color;
}