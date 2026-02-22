#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 oneTexel; //whats this used for

uniform float Direction;
uniform float BlurStrength;

out vec4 fragColor;

void main() {
    fragColor.xyz = texture(DiffuseSampler, texCoord).xyz;
    fragColor.a = 1.0 - 1e-6 * (Direction + BlurStrength);
}
