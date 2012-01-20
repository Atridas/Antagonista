// ******* color.vs

#line 4

#if __VERSION__ < 330
  #define out varying
  
  attribute vec3 a_v3Position;
  attribute vec4 a_v4Color;
  
  varying vec4 v_v4Color;
  
  uniform mat4 u_m4ModelViewProjection;
#else
  layout(location = 0) in vec3 a_v3Position;
  layout(location = 7) in vec4 a_v4Color;
  
  out vec4 v_v4Color;
  
  layout(std140) uniform UniformInstances {
    struct {
      mat4 m4ModelViewProjection;
      mat4 m4ModelView;
      mat4 m4ModelViewIT;
    } u_InstanceInfo[MAX_INSTANCES];
  };

  #define u_m4ModelViewProjection u_InstanceInfo[gl_InstanceID].m4ModelViewProjection
  #define u_m4ModelView           u_InstanceInfo[gl_InstanceID].m4ModelView
  #define u_m4ModelViewIT         u_InstanceInfo[gl_InstanceID].m4ModelViewIT
#endif
  
// -------------------------------------------------------------------------
void main()
{
  v_v4Color    = a_v4Color;
  gl_Position  = u_m4ModelViewProjection * vec4(a_v3Position,1.0);
}
  
  