#version 150

//#moj_import <minecraft:globals.glsl> //GameTime

uniform sampler2D InSampler;

in vec2 texCoord;

layout(std140) uniform DoubleVisionConfig {
    float EffectStrength; //NOTE: could use a default uniform
};

out vec4 fragColor;


void main()
{
  vec2 leftCoord = texCoord * vec2(1.0 + EffectStrength, 1.0);
  vec2 rightCoord = leftCoord - vec2(EffectStrength, 0.0);

  vec3 left_color = texture(InSampler, leftCoord).xyz;
  vec3 right_color = texture(InSampler, rightCoord).xyz;

  fragColor.xyz = 0.5 * (left_color + right_color);
}
