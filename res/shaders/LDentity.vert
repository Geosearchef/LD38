#version 400 core

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;

uniform vec3 lightPositionEyeSpace[4];
uniform vec3 sunPosition;
uniform float useFakeLighting;

uniform float fogDensity;
uniform float fogGradient;

uniform float numberOfRows;
uniform vec2 offset;

uniform vec4 plane;
uniform mat4 lightSpaceMatrix;

uniform float hasNormalMap;

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textureCoords;
layout (location = 2) in vec3 normal;
layout (location = 3) in vec3 tangent;

out vec2 pass_textureCoords;
out vec3 pass_tangent;
out vec3 surfaceNormal;
out vec3 toLightVector[4];
out vec3 toCameraVector;
out vec3 toCameraWorldVector;
out float visibility;
out vec4 FragPosLightSpace;

out vec3 shadowNormal;
out vec3 toSunVectorShadow;


void main(void)
{
	if(hasNormalMap == 1)
	{
		pass_tangent = tangent;
	}
	
	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
	
	
	gl_ClipDistance[0] = dot(worldPosition, plane);
	
	mat4 modelViewMatrix = viewMatrix * transformationMatrix;
	vec4 positionRelativeToCam = modelViewMatrix * vec4(position,1.0);
	gl_Position = projectionMatrix * positionRelativeToCam;
	
	toCameraWorldVector = -positionRelativeToCam.xyz;
	
	pass_textureCoords = (textureCoords / numberOfRows) + offset;
	
	vec3 actualNormal = normal;
	if(useFakeLighting > 0.5)
	{
		actualNormal = vec3(0.0, 1.0, 0.0);
	}
	
	shadowNormal = (transformationMatrix * vec4(actualNormal, 0.0)).xyz;
	toSunVectorShadow = sunPosition - worldPosition.xyz;
	
	surfaceNormal = (modelViewMatrix * vec4(normal,0.0)).xyz;
	
	
	if(hasNormalMap == 1)
	{
		vec3 norm = normalize(surfaceNormal);
		vec3 tang = normalize((modelViewMatrix * vec4(tangent, 0)).xyz);
		vec3 bitang = normalize(cross(norm, tang));
		
		mat3 toTangentSpace = mat3(
			tang.x, bitang.x, norm.x,
			tang.y, bitang.y, norm.y,
			tang.z, bitang.z, norm.z
		);
		
		for(int i = 0;i < 4;i++)
		{
			toLightVector[i] = toTangentSpace * lightPositionEyeSpace[i] - positionRelativeToCam.xyz;
		}
		toCameraVector = toTangentSpace * (-positionRelativeToCam.xyz);
	}
	else
	{
		for(int i=0;i<4;i++){
			toLightVector[i] = lightPositionEyeSpace[i] - positionRelativeToCam.xyz;
		}
		toCameraVector = -positionRelativeToCam.xyz;
	}
	
	
	
	float distanceToCam = length(positionRelativeToCam.xyz);
	visibility = exp(-pow((distanceToCam * fogDensity), fogGradient));
	visibility = clamp(visibility, 0.0, 1.0);
	
	FragPosLightSpace = lightSpaceMatrix * worldPosition;
}