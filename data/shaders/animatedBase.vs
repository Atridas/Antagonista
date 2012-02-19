// ******* base.vs

// nota: farem 3 perfils:
// bàsic (OpenGL 2.1, GLSL 1.20)
// mig   (OpenGL 3.3, GLSL 3.30)
// alt   (OpenGL 4.2, GLSL 4.20)

#line 9

// Vertex Atributes ------------------------------------
layout(location = 0) in vec3 a_v3Position;
layout(location = 1) in vec3 a_v3Normal;
layout(location = 2) in vec3 a_v3Tangent;
layout(location = 3) in vec3 a_v3Bitangent;
layout(location = 4) in vec2 a_v2UV;

// Vertex transformed info ------------------------------------
out vec3 v_v3Position; //View space position
out vec3 v_v3Normal;      //View
out vec3 v_v3Tangent;
out vec3 v_v3Bitangent;
out vec2 v_v2UV;

// Uniforms -----------------------------------------------------
layout(std140) uniform UniformInstances {
  //struct {
    mat4 u_m4ModelViewProjection;
    mat4 u_m4ModelView;
    mat4 u_m4ModelViewIT;
  //} u_InstanceInfo[MAX_INSTANCES];
};
  
layout(std140) uniform ArmatureInstances {
  //struct {
    mat4x3 u_m43BonePalete[MAX_BONES];
  //} u_InstanceInfo[MAX_INSTANCES];
};
  
// -------------------------------------------------------------------------
void main()
{
  gl_Position   =  u_m4ModelViewProjection * vec4(a_v3Position,1.0);
  v_v3Position  = (u_m4ModelView           * vec4(a_v3Position,1.0)).xyz;
  v_v3Normal    = (u_m4ModelViewIT         * vec4(a_v3Normal,0.0)  ).xyz;
  v_v3Tangent   = (u_m4ModelView           * vec4(a_v3Tangent,0.0)  ).xyz;
  v_v3Bitangent = (u_m4ModelView           * vec4(a_v3Bitangent,0.0)  ).xyz;
  v_v2UV = a_v2UV;
}
  
  