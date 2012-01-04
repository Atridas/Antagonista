// ******* base.vs

// nota: farem 3 perfils:
// bàsic (OpenGL 2.1, GLSL 1.20)
// mig   (OpenGL 3.3, GLSL 3.30)
// alt   (OpenGL 4.2, GLSL 4.20)

#line 9

//Parametrització
//TANGENTS
//ANIMATED

#if __VERSION__ < 330
  #define in attribute 
  #define out varying
#endif

// Vertex Atributes ------------------------------------
in vec3 a_v3Position;
in vec3 a_v3Normal;
#if defined(TANGENTS)
  in vec3 a_v3Tangent;
  in vec3 a_v3Bitangent;
#endif
in vec2 a_v2UV;

#if defined(ANIMATED)
  in ivec4 a_i4BlendIndexs;
  in vec4  a_v4BlendWeights;
#endif

// Vertex transformed info ------------------------------------
out vec3 v_v3Position; //View space position
out vec3 v_v3Normal;      //View
#if defined(TANGENTS)
  out vec3 v_v3Tangent;   //View
  out vec3 v_v3Bitangent; //View
#endif
out vec2 v_v2UV;

// Uniforms -----------------------------------------------------
#if __VERSION__ < 330
  uniform mat4 u_m4ModelViewProjection;
  uniform mat4 u_m4ModelView;
  #if defined(ANIMATED)
    uniform mat3x4 u_m32Bones[MAX_BONES];
  #endif
  
#else
  layout(std140) uniform UniformInstances {
    struct {
      mat4 m4ModelViewProjection;
      mat4 m4ModelView;
      #if defined(ANIMATED)
        mat3x4 m34Bones[MAX_BONES];
      #endif
    } u_InstanceInfo[MAX_INSTANCES];
  };
  
  #define u_m4ModelViewProjection u_InstanceInfo[gl_InstanceID].m4ModelViewProjection
  #define u_m4ModelView           u_InstanceInfo[gl_InstanceID].m4ModelView
  #define u_m34Bones              u_InstanceInfo[gl_InstanceID].m34Bones
#endif
  
  
// -------------------------------------------------------------------------
void main()
{
  //TODO animated

  gl_Position  =  u_m4ModelViewProjection * vec4(a_v3Position,1.0);
  v_v3Position = (u_m4ModelView           * vec4(a_v3Position,1.0)).xyz;
  v_v3Normal   = (u_m4ModelView           * vec4(a_v3Normal,0.0)  ).xyz;
  #if defined(TANGENTS)
    v_v3Tangent   = (u_m4ModelView        * vec4(a_v3Tangent,0.0)  ).xyz;
    v_v3Bitangent = (u_m4ModelView        * vec4(a_v3Bitangent,0.0)).xyz;
  #endif
  v_v2UV = a_v2UV;
}
  
  