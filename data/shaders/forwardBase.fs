// ******* forwardBase.fs

// nota: farem 3 perfils:
// bàsic (OpenGL 2.1, GLSL 1.20)
// mig   (OpenGL 3.3, GLSL 3.30)
// alt   (OpenGL 4.2, GLSL 4.20)

//Parametrització
//TANGENTS
//PARALLAX

#line 13

// Vertex transformed info ------------------------------------
in vec3 v_v3Position; //View space position
in vec3 v_v3Normal;      //View
in vec2 v_v2UV;

layout(location = 0)out vec4 f_v4Color;

// Uniforms ----------------------------------------

uniform sampler2D u_s2Albedo;

layout(std140) uniform UniformLight
{
  vec3 u_v3AmbientLight;
  vec3 u_v3DirectionalLightDirection;
  vec3 u_v3DirectionalLightColor;
};

layout(std140) uniform UniformMaterials
{
  float u_fSpecularFactor;
  float u_fGlossiness;
  float u_fHeight;
};

//----------------------------------------------------------
void main()
{
  //TODO PARALLAX
  #define l_v2UV v_v2UV

  vec4 l_v4TexColor = texture(u_s2Albedo, l_v2UV);
  
  //TODO TANGENTS
  vec3 l_v3Normal = normalize(v_v3Normal);
  
  vec3 l_v3AmbientColor = l_v4TexColor.rgb * u_v3AmbientLight;
  
  //vec3 l_v3DirectionToLight = normalize(u_v3DirectionalLightPosition - v_v3Position);
  vec3 l_v3DiffuseColor = l_v4TexColor.rgb * u_v3DirectionalLightColor * dot(l_v3Normal, -u_v3DirectionalLightDirection);
  l_v3DiffuseColor = max(l_v3DiffuseColor, vec3(0,0,0));
  
  vec3 R = 2 * dot(l_v3Normal, -u_v3DirectionalLightDirection) * l_v3Normal + u_v3DirectionalLightDirection;
  vec3 V = normalize(-v_v3Position);
  vec3 l_v3SpecularColor = l_v4TexColor.rgb * u_v3DirectionalLightColor * u_fSpecularFactor * pow(max(0,dot(R,V)),u_fGlossiness);
  
  f_v4Color = vec4(l_v3AmbientColor + l_v3DiffuseColor + l_v3SpecularColor, l_v4TexColor.a );
  f_v4Color = clamp(f_v4Color, 0, 1);
  
  //f_v4Color = mix(vec4(u_v3AmbientLight,1), f_v4Color, 0.1);
  
  /*
  vec4 aux = texture(u_s2Albedo, v_v2UV);
  vec4 aux2 = vec4(1,1,1,1);
  vec4 aux3 = mix(aux, aux2, 0.1);
  vec4 aux4 = clamp(vec4(v_v3Normal.x, v_v3Normal.y, v_v3Normal.z, 1),0,1);
  f_v4Color = mix(aux4, aux3, 0.1);
  */
}

