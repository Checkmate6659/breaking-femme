#version 150

uniform sampler2D DiffuseSampler;

//other fun uniforms in PostEffectPass.render
//also https://github.com/McTsts/Minecraft-Shaders-Wiki/blob/main/Uniforms.md
in vec2 texCoord;
in vec2 oneTexel; //size of a single pixel in UV coordinates

//TODO: figure out why Time and GameTime are NOT WORKING
uniform float Timer; //from 0 to 1, modulo 2^16 ticks
uniform float Saturation; //from -1 to 1, where -1 is completely black and white (C = 0) and 1 is completely saturated (C = 1)
uniform float Rotation; //from 0 to 1, where 0 is no color rotation effect and 1 is full effect (0.5 periodically goes through the center spot, i.e. desaturation)
uniform float WarpStrength; //from 0 to 1
uniform float GlitchStrength; //from 0 to 1

out vec4 fragColor;

//pseudo-random hash of t, between 0 and 1
float getRandom(float t)
{
    return fract(sin(t * 528.98) * 674.2069);
}


//OKLab implementation: https://bottosson.github.io/posts/oklab/
float cbrt(float x)
{
    return pow(x, 1./3.);
}

vec3 linear_srgb_to_oklab(vec3 c) 
{
    float l = 0.4122214708 * c.r + 0.5363325363 * c.g + 0.0514459929 * c.b;
	float m = 0.2119034982 * c.r + 0.6806995451 * c.g + 0.1073969566 * c.b;
	float s = 0.0883024619 * c.r + 0.2817188376 * c.g + 0.6299787005 * c.b;

    float l_ = cbrt(l);
    float m_ = cbrt(m);
    float s_ = cbrt(s);

    return vec3(
        0.2104542553*l_ + 0.7936177850*m_ - 0.0040720468*s_,
        1.9779984951*l_ - 2.4285922050*m_ + 0.4505937099*s_,
        0.0259040371*l_ + 0.7827717662*m_ - 0.8086757660*s_
    );
}

vec3 oklab_to_linear_srgb(vec3 c) 
{
    float l_ = c.x + 0.3963377774 * c.y + 0.2158037573 * c.z;
    float m_ = c.x - 0.1055613458 * c.y - 0.0638541728 * c.z;
    float s_ = c.x - 0.0894841775 * c.y - 1.2914855480 * c.z;

    float l = l_*l_*l_;
    float m = m_*m_*m_;
    float s = s_*s_*s_;

    return vec3(
		 4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s,
		-1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s,
		-0.0041960863 * l - 0.7034186147 * m + 1.7076147010 * s
    );
}

//calculate saturation in OKLab coordinates
vec3 saturate_lab(vec3 c)
{
    float old_chroma = length(c.yz);
    float new_chroma = pow(old_chroma, exp(atanh(-Saturation)));
    return vec3(c.x, c.yz * new_chroma / old_chroma);
}

vec3 modify_rgb(vec3 c)
{
    c = linear_srgb_to_oklab(c);
    c = saturate_lab(c);
    //rotate color according to time, and blend it
    float angle = 6.283185307179586 * Timer * 205.; //about 1 full rotation every 16 seconds
    vec3 new_c = vec3(c.x, c.y * cos(angle) + c.z * sin(angle), c.y * -sin(angle) + c.z * cos(angle));
    return oklab_to_linear_srgb(c * (1. - Rotation) + new_c * Rotation);
}

vec3 getModifiedColorAt(vec2 pos)
{
    return modify_rgb(texture(DiffuseSampler, pos).xyz);
}


//these following 2 functions are pseudo-3d gradient noise
//half the gpu cost of perlin noise by using rotating gradient vectors instead of normie extra sampling
//t: should loop properly between 0 and 1
vec2 gradient(vec2 intPos, float t) {
    float rand = fract(sin(dot(intPos, vec2(12.9898, 78.233))) * 43758.5453);;
    float rand2 = fract(sin(69.420 + dot(intPos, vec2(129.898, 78.233))) * 43758.5453);;
    
    //rotate gradient: random starting rotation, random rotation rate, but always one that loops properly!
    float speed = (rand2 - 0.5) * 4172.151340188181; //setting maximum rotation speed (depends on timescale)
    float angle = 6.283185307179586 * (rand + t * floor(speed) + 0.5 * sign(speed) + 0.5); //needs to be a non-zero integer
    return vec2(cos(angle), sin(angle));
}

float pseudo3dNoise(vec2 pos, float t) {
    vec2 i = floor(pos);
    vec2 f = pos - i;
    vec2 blend = f * f * (3.0 - 2.0 * f);
    float noiseVal = 
        mix(
            mix(
                dot(gradient(i + vec2(0, 0), t), f - vec2(0, 0)),
                dot(gradient(i + vec2(1, 0), t), f - vec2(1, 0)),
                blend.x),
            mix(
                dot(gradient(i + vec2(0, 1), t), f - vec2(0, 1)),
                dot(gradient(i + vec2(1, 1), t), f - vec2(1, 1)),
                blend.x),
        blend.y
    );
    return noiseVal * 1.4215; //1.4216 too much, 1.4215 seems to still land in [-1; 1]
}

//in this function the characteristic distances the same over both axes the same; it is given in pixels as argument
//t: should loop properly between 0 and 1
//chard: characteristic distance of warping, in pixels
//warp: how far, in multiples of chard, can a warp take a point from its original location
vec2 computeWarping(vec2 pos, float t, float chard, float warp)
{
    pos /= (oneTexel * chard); //characteristic distance is set here; pos is now in units of characteristic distances: [0; screenx/chard] * [0; screeny/chard]
    vec2 offset = vec2(1.0 / (oneTexel.x * chard) + 1.0, 0); //how much to shift by to avoid overlapping noise

    vec2 pos2 = vec2(pseudo3dNoise(pos + offset, t), pseudo3dNoise(pos + offset + offset, t)); //in [-1; 1]^2 coordinates
    pos += pos2 * warp; //warp pos a tiny bit (warp * chard in pixels), now its in [-warp; screenx/chard + warp] * [-warp; screeny/chard + warp]

    //need pos to be back in [0; screenx/chard] * [0; screeny/chard], without distorting the image (screenmin = min(screenx, screeny))
    float screenmin_over_chard = 1. / (max(oneTexel.y, oneTexel.x) * chard); //actually oneTexel.y is usually the max, but doing this for completeness's sake
    pos = (pos + vec2(warp)) * screenmin_over_chard / (screenmin_over_chard + warp + warp); //back to [0; screenx/chard] * [0; screeny/chard]

    return pos * oneTexel * chard; //return to regular UV coordinates
}



//these 2 functions are for dithering
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
    vec2 coord = texCoord;

    //apply warp, char distance of 256 pixels
    coord = computeWarping(coord, Timer, 256.0, WarpStrength);

    if(GlitchStrength == 0.0) //be able to disable glitch
    {
        fragColor.xyz = getModifiedColorAt(coord);
    }
    else
    {
        float strength4 = GlitchStrength * GlitchStrength;
        strength4 *= strength4;
        for(float i = 0.0; i < 95.0; i += 6.0) //do 16 lines at most a time
            if(getRandom(Timer + i + 3.0) < strength4) //should there be a glitch? more strength => more glitchy
            {
                float y = getRandom(Timer + i);
                float dy = oneTexel.y;
                if(GlitchStrength >= 0.75) //do a 4 pixel block if downscaled (strength >= 0.75)
                {
                    dy *= 4.0;
                    y -= mod(y, dy);
                }
                float x1 = getRandom(Timer + i + 1.0) * 1.25 - 0.25; //these scalings make full glitch lines possible
                float x2 = getRandom(Timer + i + 2.0) * 1.25;
                float align = (coord.x - x1) / (x2 - x1);
                if(align > 0 && align < 1 && coord.y > y && coord.y < y + dy) //glitch line: 1 pixel tall
                    coord = vec2(mod(coord.x + getRandom(Timer + i + 4.0), 1.0), mod(coord.y + getRandom(Timer + i + 5.0), 1.0));
            }

        //move r, g and b around by a random amount
        float delta = GlitchStrength * GlitchStrength * 0.015625;
        float i = (coord.y > getRandom(Timer - 1.0) * 4.0 / GlitchStrength) ? 3.0 : 0.0; //screen torn at random y level, more probability of there being a cut with higher strength, at most 1/4
        coord = vec2(coord.x * (1. + delta), coord.y); //do a tiny stretch
        vec3 col = vec3(
            getModifiedColorAt(coord - vec2(getRandom(Timer + i - 5.0) * delta, 0.0)).x,
            getModifiedColorAt(coord - vec2(getRandom(Timer + i - 6.0) * delta, 0.0)).y,
            getModifiedColorAt(coord - vec2(getRandom(Timer + i - 7.0) * delta, 0.0)).z
        );

        if(GlitchStrength >= 0.25)
        {
            ivec2 mask_coord;
            if(GlitchStrength < 0.5)
                mask_coord = ivec2(mod(texCoord / oneTexel, 4.0)); //we need the real texCoord here, otherwise dithering gets distorted
            else
                mask_coord = ivec2(mod(0.5 * texCoord / oneTexel, 4.0)); //half resolution

            if(GlitchStrength < 0.75)
                col = step(getBayesMask4(mask_coord), col);
            else
                col = step(getBayesMask2(mask_coord), col); //worse dithering
        }

        fragColor.xyz = col;
    }

    fragColor.a = 1.0;
}
