precision mediump float;

uniform sampler2D u_Texture;

uniform sampler2D u_ProcessedTexture;

uniform int u_IsPortrait;

uniform float u_ViewWidth;

uniform float u_ViewHeight;

varying vec2 v_TexCoordinate;

uniform float u_ClickXCoord;

uniform float u_ClickYCoord;

uniform float u_BlurRadius;

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
         vec2 scale = vec2(u_ViewWidth, u_ViewHeight);
         vec2 flipped_texcoord = vec2(v_TexCoordinate.x, 1.0 - v_TexCoordinate.y);
         gl_FragColor = texture2D(u_Texture, flipped_texcoord);
         float flippedHeight = u_ViewHeight - u_ClickYCoord;
         float x_pow = (flippedHeight-gl_FragCoord.y)*(flippedHeight-gl_FragCoord.y);
         float y_pow = (u_ClickXCoord-gl_FragCoord.x)*(u_ClickXCoord-gl_FragCoord.x);

         if(sqrt(x_pow+y_pow)<u_BlurRadius)
         {

                float denom = float((2.0*u_KernelHalfLength+1.0)*(2.0*u_KernelHalfLength+1.0));
                gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
                for(int j = -int(u_KernelHalfLength); j<= int(u_KernelHalfLength); j++)
                {
                     gl_FragColor += texture2D(u_ProcessedTexture, flipped_texcoord + vec2(float(j), 0)/scale);
                }


         }
}