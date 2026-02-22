#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;

uniform float EffectStrength;
uniform float Resolution;
uniform float MosaicSize;

out vec4 fragColor;

void main() {
    //i cant get rid of this fucking code otherwise it black screens??
    vec2 mosaicInSize = InSize / MosaicSize;
    vec2 fractPix = fract(texCoord * mosaicInSize) / mosaicInSize;

    vec4 baseTexel = texture(DiffuseSampler, texCoord - fractPix);

    vec3 fractTexel = baseTexel.rgb - fract(baseTexel.rgb * Resolution) / Resolution;
    float luma = dot(fractTexel, vec3(0.3, 0.59, 0.11));
    vec3 chroma = (fractTexel - luma);
    baseTexel.rgb = luma + chroma;
    baseTexel.a = 1.0;

    fragColor = baseTexel;


    //my own shit goes here
    vec2 rightCoord = texCoord * vec2(1.0 - EffectStrength, 1.0);
    vec2 leftCoord = rightCoord + vec2(EffectStrength, 0.0);

    vec3 rightColor = texture(DiffuseSampler, rightCoord).xyz;
    vec3 leftColor = texture(DiffuseSampler, leftCoord).xyz;

    fragColor.xyz = 0.5 * (rightColor + leftColor);
}
