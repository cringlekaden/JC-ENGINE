void main()
{
    vec3 normal = normalize(tbnMatrix * (255.0/128.0 * texture(normalMap, texCoord0.xy).xyz - 1));
    fragColor = texture(diffuse, texCoord0.xy) * CalcLightingEffect(normal, worldPos0);
}