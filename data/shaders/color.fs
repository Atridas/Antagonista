// ******* color.fs

#line 13

#if __VERSION__ < 330
  attribute vec4 a_vColor;
  
  #define f_v4Color gl_FragColor
#else
  in vec4 v_v4Color;
  
  out vec4 f_v4Color;
#endif


//----------------------------------------------------------
void main()
{
  f_v4Color = v_v4Color;
}
