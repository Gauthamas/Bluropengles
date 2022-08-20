attribute vec4 a_Position;

attribute vec2 a_TexCoordinate;

uniform int u_IsPortrait;

varying vec2 v_TexCoordinate;

uniform float u_ViewWidth;

uniform float u_ViewHeight;

uniform float u_AspectRatio;

/*
In case of portrait mode we want to occupy the entire width and scale to height accordingly
height occupied View = view width/aspect ratio

Currently 2 units of width is occupying whole phone width
Similarly 2 units of phone height occupies phone height
What should be the scaling such that height occupied maintains
correct ratio
scale = view height/(view width/aspect ratio)
scale = (view height*aspect ratio)/view width


In case of landscape mode we want to occupy the entire height and scale the width accordingly

Width occupied View = View height * aspect ratio

In current case phone width is height.

scale = view width/(view height * aspect ratio)
*/



void main()
{
            //Image width/height

            vec2 scale = vec2(u_ViewWidth/(u_ViewHeight*u_AspectRatio), 1.0);
            if(u_IsPortrait == 1)
            {
                scale = vec2(1.0, (u_ViewHeight*u_AspectRatio)/u_ViewWidth);
            }
            v_TexCoordinate = scale*(a_TexCoordinate - 0.5)+0.5;
            gl_Position = a_Position;
}
