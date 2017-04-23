#version 400 core

uniform sampler2D textureSampler;
uniform samplerCube cubeMap;
uniform sampler2D shadowMap;
uniform sampler2D normalMap;
uniform sampler2D atmMap;

uniform vec4 color;

uniform vec3 lightColor[4];
uniform vec3 attenuation[4];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;
uniform float environmentReflectivity;
uniform float castShadow;
uniform float ambientLighting;

uniform float hasNormalMap;
uniform float hasATMMap;

in vec2 pass_textureCoords;
in vec3 pass_tangent;
in vec3 surfaceNormal;
in vec3 toLightVector[4];
in vec3 toCameraVector;
in vec3 toCameraWorldVector;
in float visibility;
in vec4 FragPosLightSpace;

in vec3 shadowNormal;
in vec3 toSunVectorShadow;

out vec4 out_Color;

const float minBias = 0.005;
const float maxBias = 0.05;

//LD38
uniform int highlight;



float linearSampling(sampler2D shadowMap, vec2 coords, vec2 texelSize, float compare)
{
	vec2 pixelPos = coords / texelSize + vec2(0.5);
	vec2 fracPart = fract(pixelPos);
	vec2 startTexel = (pixelPos - fracPart) * texelSize;
	
	float blTexel = step(compare, texture(shadowMap, startTexel).r);
	float brTexel = step(compare, texture(shadowMap, startTexel + vec2(texelSize.x, 0.0)).r);
	float tlTexel = step(compare, texture(shadowMap, startTexel + vec2(0.0, texelSize.y)).r);
	float trTexel = step(compare, texture(shadowMap, startTexel + texelSize).r);
	
	float mixL = mix(blTexel, tlTexel, fracPart.y);
	float mixR = mix(brTexel, trTexel, fracPart.y);
	
	return 1.0 - mix(mixL, mixR, fracPart.x);
}

float ShadowCalculation(vec4 fragPosLightSpace, vec3 unitNormal, vec3 lightDir)
{
	float bias = max(maxBias * (1.0 - dot(unitNormal, lightDir)), minBias);
	
    vec3 projCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;
    projCoords = projCoords * 0.5 + 0.5;
    float currentDepth = projCoords.z;
    
    vec2 texelSize = 1.0 / textureSize(shadowMap, 0);
	
	float shadow = linearSampling(shadowMap, projCoords.xy, texelSize, projCoords.z - bias) ; 
	
	if(projCoords.x > 0.98 || projCoords.y > 0.98 || projCoords.x < 0.02 || projCoords.y < 0.02 || projCoords.z > 1)
	{
		shadow = 0;
	}
	
    return shadow;
}




void main(void)
{	
	vec4 textureColor = texture(textureSampler, pass_textureCoords);
	if(textureColor.a < 0.5)
		discard;
	
	
	vec3 unitNormal = normalize(surfaceNormal);
	vec3 unitVectorToCamera = normalize(toCameraVector);
	
	if(hasNormalMap == 1)
	{
		vec4 normalMapValue = 2.0 * texture(normalMap, pass_textureCoords) - 1.0;
		unitNormal = normalize(normalMapValue.rgb);
	}
	
	float shadow = 0.0;
	if(castShadow == 1)
	{
		shadow = ShadowCalculation(FragPosLightSpace, normalize(shadowNormal), toSunVectorShadow);
	}
	 
	vec3 totalDiffuse = vec3(0.0, 0.0, 0.0);
	vec3 totalSpecular = vec3(0.0, 0.0, 0.0);
	
	if(shadow != 1.0)
	{
		for(int i = 0;i < 4;i++)
		{
			float distance = length(toLightVector[i]);
			float attenuationFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);
			vec3 unitLightVector = normalize(toLightVector[i]);
			float dotProduct = dot(unitNormal, unitLightVector);
			float brightness = max(dotProduct, 0.0);	
			
			vec3 lightDirection = -unitLightVector;
			vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
			
			float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
			specularFactor = max(specularFactor, 0.0);
			float dampedFactor = pow(specularFactor, shineDamper);
			if(i == 0)
			{
				totalDiffuse = (totalDiffuse + (brightness * lightColor[i]) / attenuationFactor) * (1.0 - shadow);
				totalSpecular = (totalSpecular + (dampedFactor * reflectivity * lightColor[i]) / attenuationFactor) * (1.0 - shadow);
			}
			else
			{
				totalDiffuse = totalDiffuse + (brightness * lightColor[i]) / attenuationFactor;
				totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColor[i]) / attenuationFactor;
			}
		}
	}
	
	//Minium is ambient lighting
	totalDiffuse = max(totalDiffuse, ambientLighting);
	
	
	//Use additional texture map for specular lighting and self illumination
	if(hasATMMap == 1)
	{
		vec4 atmMapColor = texture(atmMap, pass_textureCoords);
		totalSpecular = totalSpecular * atmMapColor.r;
		totalDiffuse = max(totalDiffuse, atmMapColor.g);
	}
	
	//Reflection / Refraction
	vec3 unitWorldVectorFromCamera = -toCameraWorldVector;
	
	vec3 reflectivityColor = vec3(0, 0, 0);
	if(environmentReflectivity > 0.09)
	{
		vec3 reflected = reflect(unitWorldVectorFromCamera, shadowNormal);
		reflectivityColor = (texture(cubeMap, reflected)).xyz;
		reflectivityColor = reflectivityColor * textureColor.xyz;
	}
	
	
	out_Color = vec4(totalDiffuse, 1.0) * textureColor + vec4(totalSpecular, 1.0);
	out_Color = mix(out_Color, vec4(reflectivityColor, 1.0), environmentReflectivity);
	out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);
	out_Color = out_Color * color;
	
	if(highlight == 1) {
		out_Color *= vec4(1.2, 1.2, 1.2, 1.0);
	}
	
	//out_Color = vec4(length(toLightVector[1]) / 10.0, 0, 0, 1.0);
	//out_Color = vec4(shadowNormal, 1.0);
}