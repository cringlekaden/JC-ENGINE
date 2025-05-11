#include "sampling.glh"

void main() {
    vec3 directionToEye = normalize(C_cameraPosition - worldPos0);
    vec2 texCoords = CalcParallaxTexCoords(dispMap, tbnMatrix, directionToEye, texCoord0, dispMapScale, dispMapBias);
	vec3 normal = normalize(tbnMatrix * (255.0/128.0 * texture(normalMap, texCoords).rgb - 1));
	fragColor = texture(diffuse, texCoords) * CalcLightingEffect(normal, worldPos0);
}
