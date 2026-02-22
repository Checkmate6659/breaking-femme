#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform float Direction; //0 for vertical, 1 for horizontal; the gaussian blur is separable!
uniform float BlurStrength; //in pixels

out vec4 fragColor;

float gauss(float r, float invstd2)
{
    //constant 1/(std * sqrt(2pi)) is NOT required due to normalization
    //invstd2 is sigma^-2, precomputed for efficiency
    return exp(-r*r * 0.5 * invstd2);
}

void main() {
    vec2 dirvector = vec2(Direction, 1.0 - Direction);
    float invstd2 = 1.0 / (BlurStrength * BlurStrength);

    float normalization_constant = 0.0;
    vec3 color_avg = vec3(0.0);
    float r0 = BlurStrength * 3.0; //im neglecting the rest of the distrib (3sigma), its so tiny
    float dr = 2.0 * r0 / ceil(8.0 * r0); //let dr be close to but under 0.25, but be an integer amount between -r0 and r0
    for(float r = -r0; r < r0 + 0.5 * dr; r += dr) //worst for loop ever. should have at least 1 element
    {
        float weight = gauss(r, invstd2);
        normalization_constant += weight;
        color_avg += texture(DiffuseSampler, texCoord + oneTexel * r * dirvector).xyz * weight;
    }

    fragColor.xyz = color_avg / normalization_constant; //should NOT divide by 0
    fragColor.a = 1.0;
}
