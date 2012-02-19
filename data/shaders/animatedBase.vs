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

layout(location = 5) in ivec4 a_i4BlendIndexs;
layout(location = 6) in  vec4 a_v4BlendWeights;

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
    mat4x3 u_m43BonePaleteIT[MAX_BONES];
  //} u_InstanceInfo[MAX_INSTANCES];
};


vec4 animate(in vec4 _v4Point) {

  return _v4Point;

  //return vec4(u_m43BonePalete[ a_i4BlendIndexs.x ] * _v4Point , 1);

  vec3 result1 = u_m43BonePalete[ a_i4BlendIndexs.x ] * _v4Point;
  vec3 result2 = u_m43BonePalete[ a_i4BlendIndexs.y ] * _v4Point;
  vec3 result3 = u_m43BonePalete[ a_i4BlendIndexs.z ] * _v4Point;
  vec3 result4 = u_m43BonePalete[ a_i4BlendIndexs.w ] * _v4Point;

  //vec3 result1 = u_m43BonePalete[ 1 ] * _v4Point;
  //vec3 result2 = u_m43BonePalete[ 1 ] * _v4Point;
  //vec3 result3 = u_m43BonePalete[ 1 ] * _v4Point;
  //vec3 result4 = u_m43BonePalete[ 1 ] * _v4Point;
  
  return vec4( result1 * a_v4BlendWeights.x
             + result2 * a_v4BlendWeights.y
             + result3 * a_v4BlendWeights.z
             + result4 * a_v4BlendWeights.w
             //+ (1 - dot(a_v4BlendWeights,a_v4BlendWeights)) * _v4Point.xyz
             , 1);
             
}

vec4 animateIT(in vec4 _v4Point) {

  return _v4Point;
  
  vec3 result1 = _v4Point * transpose(u_m43BonePaleteIT[ a_i4BlendIndexs.x ]);
  vec3 result2 = _v4Point * transpose(u_m43BonePaleteIT[ a_i4BlendIndexs.y ]);
  vec3 result3 = _v4Point * transpose(u_m43BonePaleteIT[ a_i4BlendIndexs.z ]);
  vec3 result4 = _v4Point * transpose(u_m43BonePaleteIT[ a_i4BlendIndexs.w ]);
  
  return vec4( result1 * a_v4BlendWeights.x
             + result2 * a_v4BlendWeights.y
             + result3 * a_v4BlendWeights.z
             + result4 * a_v4BlendWeights.w
             , 1);
}
  
// -------------------------------------------------------------------------
void main()
{
  gl_Position   =  u_m4ModelViewProjection * animate  (vec4(a_v3Position,1.0 ) );
  v_v3Position  = (u_m4ModelView           * animate  (vec4(a_v3Position,1.0 ) )).xyz;
  v_v3Normal    = (u_m4ModelViewIT         * animateIT(vec4(a_v3Normal,0.0   ) )).xyz;
  v_v3Tangent   = (u_m4ModelView           * animate  (vec4(a_v3Tangent,0.0  ) )).xyz;
  v_v3Bitangent = (u_m4ModelView           * animate  (vec4(a_v3Bitangent,0.0) )).xyz;
  v_v2UV = a_v2UV;
}
  
  