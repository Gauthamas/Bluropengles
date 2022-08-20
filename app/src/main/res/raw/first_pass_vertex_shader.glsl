attribute vec4 a_Position;

attribute vec2 a_TexCoordinate;

uniform int u_IsPortrait;

varying vec2 v_TexCoordinate;

uniform float u_ViewWidth;

uniform float u_ViewHeight;

uniform float u_AspectRatio;


void main()
{
            //Image width/height
            v_TexCoordinate = a_TexCoordinate;
            gl_Position = a_Position;
}