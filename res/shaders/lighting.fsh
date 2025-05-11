in vec2 texCoord0;
in vec3 worldPos0;
in mat3 tbnMatrix;

out vec4 fragColor;

uniform float dispMapScale;
uniform float dispMapBias;
uniform sampler2D diffuse;
uniform sampler2D normalMap;
uniform sampler2D dispMap;

#include "lighting.glh"
