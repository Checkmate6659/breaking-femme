//#version 150 //cannot compile. so we write in version 110.

//#moj_import <minecraft:globals.glsl> //GameTime

uniform sampler2D DiffuseSampler;

in vec2 texCoord;

uniform float EffectStrength;

out vec4 fragColor;


void main()
{
  /*vec2 leftCoord = texCoord * vec2(1.0 + EffectStrength, 1.0);
  vec2 rightCoord = leftCoord - vec2(EffectStrength, 0.0);

  vec3 left_color = texture(DiffuseSampler, leftCoord).xyz;
  vec3 right_color = texture(DiffuseSampler, rightCoord).xyz;

  fragColor.xyz = 0.5 * (left_color + right_color);*/

  fragColor = texture(DiffuseSampler, texCoord); //show the texture
}
