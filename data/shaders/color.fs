// ******* color.fs

#line 13

#if __VERSION__ < 330
  #define f_v4Color gl_FragColor
  
  uniform vec4 u_v4SpecialColor0;
#else
  out vec4 f_v4Color;
  
  layout(std140) uniform SpecialColors {
    vec4 u_v4SpecialColor0;
    vec4 u_v4SpecialColor1;
    vec4 u_v4SpecialColor2;
    vec4 u_v4SpecialColor3;
  };
#endif


//----------------------------------------------------------
void main()
{
  f_v4Color = u_v4SpecialColor0;
}
