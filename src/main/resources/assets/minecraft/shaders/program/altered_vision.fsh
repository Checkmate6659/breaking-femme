#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 oneTexel; //whats this used for

uniform float EffectStrength;

out vec4 fragColor;

void main() {
    //my own shit goes here
    vec2 rightCoord = texCoord * vec2(1.0 - EffectStrength, 1.0);
    vec2 leftCoord = rightCoord + vec2(EffectStrength, 0.0);

    vec3 rightColor = texture(DiffuseSampler, rightCoord).xyz;
    vec3 leftColor = texture(DiffuseSampler, leftCoord).xyz;

    fragColor.xyz = 0.5 * (rightColor + leftColor);
    fragColor.a = 1.0;
}
