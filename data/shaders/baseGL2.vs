// ******* baseGL2.vs

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
  
  