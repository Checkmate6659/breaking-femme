#version 150

uniform sampler2D DiffuseSampler;

//other fun uniforms in PostEffectPass.render
//also https://github.com/McTsts/Minecraft-Shaders-Wiki/blob/main/Uniforms.md
in vec2 texCoord;
in vec2 oneTexel; //size of a single pixel in UV coordinates

//TODO: figure out why Time and GameTime are NOT WORKING
uniform float Timer; //from 0 to 1, modulo 2^16 ticks
uniform float EffectStrength; //from 0 to 1

out vec4 fragColor;

//pseudo-random hash of t, between 0 and 1
float getRandom(float t)
{
    return fract(sin(t * 528.98) * 674.2069);
}

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
    {
        fragColor.xyz = texture(DiffuseSampler, texCoord).xyz;
    }
    else
    {
        //TODO: use ascii characters or sth if EffectStrength > 0.75
        //https://prominentpainting.com/drawing-with-text-characters/
        //for that we need to declare a custom sampler (could it work with loads of regular uniforms? idk)
        //cuz declaring a massive array at the start of the shader is SLOW! every entry is an instruction: put 0 or 1 into this mem value
        //solution: you can FAKE IT! https://www.shadertoy.com/view/XljBW3

        //TODO: color rotation?
        //we could do this too: https://www.shadertoy.com/view/lsfGD2

        vec2 coord = texCoord;
        if(getRandom(Timer + 3.0) < EffectStrength) //should there be a glitch? more strength => more glitchy
        {
            float y = getRandom(Timer);
            float dy = oneTexel.y;
            if(EffectStrength >= 0.75) //do a 4 pixel block if downscaled (strength >= 0.75)
            {
                dy *= 4.0;
                y -= mod(y, dy);
            }
            float x1 = getRandom(Timer + 1.0) * 1.25 - 0.25; //these scalings make full glitch lines possible
            float x2 = getRandom(Timer + 2.0) * 1.25;
            float align = (coord.x - x1) / (x2 - x1);
            if(align > 0 && align < 1 && coord.y > y && coord.y < y + dy) //glitch line: 1 pixel tall
                coord = vec2(mod(coord.x + getRandom(Timer - 1.0), 1.0), mod(coord.y + getRandom(Timer - 2.0), 1.0));
        }

        vec3 col = texture(DiffuseSampler, coord).xyz;

        if(EffectStrength >= 0.25)
        {
            ivec2 mask_coord;
            if(EffectStrength < 0.5)
                mask_coord = ivec2(mod(texCoord / oneTexel, 4.0)); //we need the real texCoord here, otherwise dithering gets distorted
            else
                mask_coord = ivec2(mod(0.5 * texCoord / oneTexel, 4.0)); //half resolution

            if(EffectStrength < 0.75)
                col = step(getBayesMask4(mask_coord), texture(DiffuseSampler, coord).xyz);
            else
                col = step(getBayesMask2(mask_coord), texture(DiffuseSampler, coord).xyz); //worse dithering
        }

        fragColor.xyz = col;
    }

    fragColor.a = 1.0;
}
