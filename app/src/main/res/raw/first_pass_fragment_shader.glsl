precision mediump float;

uniform sampler2D u_Texture;

uniform int u_IsPortrait;

uniform float u_ViewWidth;

uniform float u_ViewHeight;

varying vec2 v_TexCoordinate;

uniform float u_ClickXCoord;

uniform float u_ClickYCoord;

uniform float u_KernelHalfLength;

bool isWithinRange(float val)
{
    if(val >= 0.0 && val <= 1.0)
    {
        return true;
    }
    return false;
}

void main()
{
      if(!isWithinRange(v_TexCoordinate.x) || !isWithinRange(v_TexCoordinate.y))
      {
            discard;
      }

       vec2 scale = vec2(3000.0, 2000.0);
       vec2 flipped_texcoord = vec2(v_TexCoordinate.x, v_TexCoordinate.y);
       gl_FragColor = texture2D(u_Texture, flipped_texcoord);
       float flippedHeight = u_ViewHeight - u_ClickYCoord;


       float denom = float((2.0*u_KernelHalfLength+1.0)*(2.0*u_KernelHalfLength+1.0));
       gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);

       for(int i = -int(u_KernelHalfLength); i <= int(u_KernelHalfLength); i++)
       {

          gl_FragColor += texture2D(u_Texture, flipped_texcoord + vec2(0, float(i))/scale)/denom;


        }

}