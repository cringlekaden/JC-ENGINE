#version 330
#include "lighting.glh"

in vec3 worldPos0;
in vec2 texCoord0;
in vec3 normal0;

out vec4 fragColor;

uniform sampler2D diffuse;
uniform SpotLight R_spotLight;

void main() {
    fragColor = texture(diffuse, texCoord0) * calculateSpotLight(R_spotLight, normalize(normal0), worldPos0);
}