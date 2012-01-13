// ******* white.fs

#line 13

#if __VERSION__ < 330
  #define f_v4Color gl_FragColor
#else
  out vec4 f_v4Color;
#endif

//----------------------------------------------------------
void main()
{
  f_v4Color = vec4(1,1,1,1);
}

