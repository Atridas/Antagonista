// ******* simple.vs

#line 4

#if __VERSION__ < 330
  #define in attribute 
  #define out varying
#endif

// Vertex Atributes ------------------------------------
in vec3 a_v3Position;
  
  
// -------------------------------------------------------------------------
void main()
{
  gl_Position  = vec4(a_v3Position,1.0);
}
  
  