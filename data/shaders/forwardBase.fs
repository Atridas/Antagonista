// nota: farem 3 perfils:
// bàsic (OpenGL 2.1, GLSL 1.20)
// mig   (OpenGL 3.3, GLSL 3.30)
// alt   (OpenGL 4.2, GLSL 4.20)

//Parametrització
//TANGENTS
//PARALLAX

#if __VERSION__ < 330
  #define in varying
  #define f_v4Color gl_FragColor
#else
  out vec4 f_v4Color;
#endif

// Vertex transformed info ------------------------------------
in vec4 v_v3VPosition; //View space position
in vec3 v_v3Normal;      //View
#if defined(TANGENTS)
  in vec3 v_v3Tangent;   //View
  in vec3 v_v3Bitangent; //View
#endif
in vec2 v_v2UV;


// Uniforms ----------------------------------------

uniform sampler2D u_s2Albedo;
#if defined(TANGENTS)
  uniform sampler2D u_s2Normalmap;
#endif

#if __VERSION__ < 330
  uniform vec3 u_v3AmbientLight;
  uniform vec3 u_v3DirectionalLightPosition;
  uniform vec3 u_v3DirectionalLightColor;
  
  uniform float u_fSpecularFactor;
  uniform float u_fGlossiness;

#else
  layout(std140) uniform UniformLight
  {
    vec3 u_v3AmbientLight;
    vec3 u_v3DirectionalLightPosition;
    vec3 u_v3DirectionalLightColor;
  };

  layout(std140) uniform UniformMaterials
  {
    float u_fSpecularFactor;
    float u_fGlossiness;
  };
#endif

//----------------------------------------------------------
void main()
{
  //TODO PARALLAX
  #define l_v2UV v_v2UV

  vec4 l_v4TexColor = texture(u_s2Albedo, l_v2UV);
  
  //TODO TANGENTS
  vec3 l_v3Normal = normalize(v_v3Normal);
  
  vec3 l_v3AmbientColor = l_v4TexColor.rgb * u_v3AmbientLight;
  
  //vec3 l_v3DirectionToLight = normalize(u_v3DirectionalLightPosition - v_v3VPosition);
  vec3 l_v3DiffuseColor = l_v4TexColor.rgb * u_v3DirectionalLight_Color * dot(l_v3Normal, -u_v3DirectionalLight_Direction);
  l_v3DiffuseColor = max(l_v3DiffuseColor, vec3(0,0,0));
  
  vec3 R = 2 * dot(l_v3Normal, -u_v3DirectionalLight_Direction) * l_v3Normal + u_v3DirectionalLight_Direction;
  vec3 V = normalize(-v_v3VPosition);
  vec3 l_v3SpecularColor = l_v4TexColor.rgb * u_v3DirectionalLight_Color * u_fSpecularFactor * pow(max(0,dot(R,V)),u_fGlossiness);
  
  f_v4Color = l_v3AmbientColor + l_v3DiffuseColor + l_v3SpecularColor;
  f_v4Color = clamp(f_v4Color, 0, 1);
};

