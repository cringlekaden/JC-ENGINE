#version 330

out vec4 fragColor;

void main() {
    float depth = gl_FragCoord.z;
    float dx = dFdx(depth);
    float dy = dFdy(depth);
    float moment2 = depth * depth + 0.25 * (dx * dx + dy * dy);
    fragColor = vec4(depth, moment2, 0.0, 0.0);
}
