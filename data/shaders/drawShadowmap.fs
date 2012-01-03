// ******* drawShadowmap.fs

// nota: farem 3 perfils:
// bàsic (OpenGL 2.1, GLSL 1.20)
// mig   (OpenGL 3.3, GLSL 3.30)
// alt   (OpenGL 4.2, GLSL 4.20)

//Parametrització
//--

#if __VERSION__ < 330
  #define in varying
  #define f_v4Depth gl_FragColor
#else
  out vec4 f_v4Depth;
#endif

// Vertex transformed info ------------------------------------
in vec4 v_v3VPosition; //View space position

//----------------------------------------------------------
void main()
{
  f_v4Depth = vec4(v_v3VPosition.zzz, 1.0);
}

