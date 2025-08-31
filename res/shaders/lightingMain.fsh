#include "sampling.glh"

bool InRange(float val) {
    return val >= 0.0 && val <= 1.0;
}

float CalcShadowAmount(sampler2D shadowMap, vec4 initialShadowMapCoords) {
    vec3 shadowMapCoords = (initialShadowMapCoords.xyz / initialShadowMapCoords.w);
    if(InRange(shadowMapCoords.x) && InRange(shadowMapCoords.y) && InRange(shadowMapCoords.z))
        return SampleVarianceShadowMap(shadowMap, shadowMapCoords.xy, shadowMapCoords.z, R_shadowVariance, R_shadowLightBleedReduction);
    return 1.0;
}

void main() {
    vec3 directionToEye = normalize(C_cameraPosition - worldPos0);
    vec2 texCoords = CalcParallaxTexCoords(dispMap, tbnMatrix, directionToEye, texCoord0, dispMapScale, dispMapBias);

    // Derivative-based edge detection from displacement map at original texcoords
    float h = texture(dispMap, texCoord0).r;
    vec2 hGrad = vec2(dFdx(h), dFdy(h));
    float gradMag = length(hGrad);
    // Thresholds for when to start and fully apply softening (tweak as needed)
    const float EDGE_START = 0.02;
    const float EDGE_END   = 0.10;
    float edge = smoothstep(EDGE_START, EDGE_END, gradMag);

    // Reduce normal map strength slightly near edges to soften transitions
    const float NORMAL_STRENGTH = 0.7;                 // base normal strength
    float normalStrength = mix(NORMAL_STRENGTH, NORMAL_STRENGTH * 0.5, edge);
    vec3 tsNormal = normalize(255.0/128.0 * texture(normalMap, texCoords).rgb - 1.0);
    tsNormal = normalize(mix(vec3(0.0, 0.0, 1.0), tsNormal, normalStrength));
    vec3 normal = normalize(tbnMatrix * tsNormal);

    // Compute lighting and apply a very gentle attenuation near strong edges
    vec4 lightingAmount = CalcLightingEffect(normal, worldPos0) * CalcShadowAmount(R_shadowMap, shadowMapCoords0);
    float edgeLightScale = mix(1.0, 0.92, edge); // up to 8% softer at strong edges
    lightingAmount *= edgeLightScale;

    fragColor = texture(diffuse, texCoords) * lightingAmount;
}
