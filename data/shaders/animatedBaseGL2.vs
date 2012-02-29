// ******* animatedBaseGL2.vs

// nota: farem 3 perfils:
// bàsic (OpenGL 2.1, GLSL 1.20)
// mig   (OpenGL 3.3, GLSL 3.30)
// alt   (OpenGL 4.2, GLSL 4.20)

#line 9

#if __VERSION__ >= 330
  #define attribute in
  #define varying out
#endif

// Vertex Atributes ------------------------------------
attribute vec3 a_v3Position;
attribute vec3 a_v3Normal;
attribute vec3 a_v3Tangent;
attribute vec3 a_v3Bitangent;
attribute vec2 a_v2UV;
attribute vec4 a_i4BlendIndexs;
attribute vec4 a_v4BlendWeights;

// Vertex transformed info ------------------------------------
varying vec3 v_v3Position; //View space position
varying vec3 v_v3Normal;      //View
varying vec3 v_v3Tangent;
varying vec3 v_v3Bitangent;
varying vec2 v_v2UV;

// Uniforms -----------------------------------------------------
uniform mat4 u_m4ModelViewProjection;
uniform mat4 u_m4ModelView;
uniform mat4 u_m4ModelViewIT;
uniform mat4x3 u_m43BonePalete[MAX_BONES];
uniform mat4x3 u_m43BonePaleteIT[MAX_BONES];
 

vec3 correctMatrixMult(in mat4x3 _m43Matrix, in vec4 _v4Vector) // fuuuu intel fuck you
{
  return vec3(_m43Matrix[0].x * _v4Vector.x + _m43Matrix[1].x * _v4Vector.y + _m43Matrix[2].x * _v4Vector.z + _m43Matrix[3].x * _v4Vector.w,
              _m43Matrix[0].y * _v4Vector.x + _m43Matrix[1].y * _v4Vector.y + _m43Matrix[2].y * _v4Vector.z + _m43Matrix[3].y * _v4Vector.w,
              _m43Matrix[0].z * _v4Vector.x + _m43Matrix[1].z * _v4Vector.y + _m43Matrix[2].z * _v4Vector.z + _m43Matrix[3].z * _v4Vector.w);
} 
 
vec4 animate(in vec4 _v4Point) {
  
  vec3 result1 = correctMatrixMult(u_m43BonePalete[ int(a_i4BlendIndexs.x) ], _v4Point);
  vec3 result2 = correctMatrixMult(u_m43BonePalete[ int(a_i4BlendIndexs.y) ], _v4Point);
  vec3 result3 = correctMatrixMult(u_m43BonePalete[ int(a_i4BlendIndexs.z) ], _v4Point);
  vec3 result4 = correctMatrixMult(u_m43BonePalete[ int(a_i4BlendIndexs.w) ], _v4Point);
  
  return vec4( result1 * a_v4BlendWeights.x
             + result2 * a_v4BlendWeights.y
             + result3 * a_v4BlendWeights.z
             + result4 * a_v4BlendWeights.w
             , 1);
             
}

vec4 animateIT(in vec4 _v4Point) {
  
  vec3 result1 = correctMatrixMult(u_m43BonePaleteIT[ int(a_i4BlendIndexs.x) ], _v4Point);
  vec3 result2 = correctMatrixMult(u_m43BonePaleteIT[ int(a_i4BlendIndexs.y) ], _v4Point);
  vec3 result3 = correctMatrixMult(u_m43BonePaleteIT[ int(a_i4BlendIndexs.z) ], _v4Point);
  vec3 result4 = correctMatrixMult(u_m43BonePaleteIT[ int(a_i4BlendIndexs.w) ], _v4Point);
  
  return vec4( result1 * a_v4BlendWeights.x
             + result2 * a_v4BlendWeights.y
             + result3 * a_v4BlendWeights.z
             + result4 * a_v4BlendWeights.w
             , 1);
}

varying vec4 v_v4Color;

// -------------------------------------------------------------------------
void main()
{
  vec4 l_v4ModelPosition = animate( vec4(a_v3Position,1.0 ) );


  gl_Position   =  l_v4ModelPosition                   * u_m4ModelViewProjection;
  v_v3Position  = (l_v4ModelPosition                   * u_m4ModelView  ).xyz;
  v_v3Normal    = (animateIT(vec4(a_v3Normal,0.0   ) ) * u_m4ModelViewIT).xyz;
  v_v3Tangent   = (animate  (vec4(a_v3Tangent,0.0  ) ) * u_m4ModelView  ).xyz;
  v_v3Bitangent = (animate  (vec4(a_v3Bitangent,0.0) ) * u_m4ModelView  ).xyz;
  v_v2UV = a_v2UV;
}
  
  