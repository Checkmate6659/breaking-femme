#version 150

uniform sampler2D DiffuseSampler;

//other fun uniforms in PostEffectPass.render
//also https://github.com/McTsts/Minecraft-Shaders-Wiki/blob/main/Uniforms.md
in vec2 texCoord;
in vec2 oneTexel; //size of a single pixel in UV coordinates

//TODO: figure out why Time and GameTime are NOT WORKING
//uniform float Time; //from 0 to 1, modulo seconds (or days? idk.)
//uniform float GameTime; //from 0 to 24000, modulo mc days (24000t), but only core shaders?
uniform float EffectStrength; //from 0 to 1

out vec4 fragColor;

float getBayesMask2(ivec2 coord)
{
    //https://en.wikipedia.org/wiki/Ordered_dithering
    //this is a 2*2 mask, with double pixel size
    float val = 0.;
    if((coord.x & 2) != 0) val += 1.;
    if((coord.y & 2) != 0) val += 2.;
    return val / 4. + 1. / 8.;
}

float getBayesMask4(ivec2 coord)
{
    //this is a 4*4 mask
    float val = 0.;
    if((coord.x & 2) != 0) val += 1.;
    if((coord.y & 2) != 0) val += 2.;
    if((coord.x & 1) != 0) val += 4.;
    if((coord.y & 1) != 0) val += 8.;
    return val / 16. + 1. / 32.;
}

void main() {
    if(EffectStrength == 0.0) //be able to disable shader
        fragColor.xyz = texture(DiffuseSampler, texCoord).xyz;
    else
    {
        //TODO: get my hands on time and add glitch effects
        //TODO: use ascii characters or sth if EffectStrength > 0.75
        //https://prominentpainting.com/drawing-with-text-characters/
        //for that we need to declare a custom sampler (could it work with loads of regular uniforms? idk)
        //cuz declaring a massive array at the start of the shader is SLOW! every entry is an instruction: put 0 or 1 into this mem value

        //TODO: color rotation?

        ivec2 mask_coord;
        if(EffectStrength < 0.25)
            mask_coord = ivec2(mod(texCoord / oneTexel, 4.0));
        else
            mask_coord = ivec2(mod(0.5 * texCoord / oneTexel, 4.0)); //half resolution

        if(EffectStrength < 0.5)
            fragColor.xyz = step(getBayesMask4(mask_coord), texture(DiffuseSampler, texCoord).xyz);
        else
            fragColor.xyz = step(getBayesMask2(mask_coord), texture(DiffuseSampler, texCoord).xyz); //worse dithering
    }

    fragColor.a = 1.0;
}
