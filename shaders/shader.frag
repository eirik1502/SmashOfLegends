#version 330 core

layout (location = 0) out vec4 color;

in DATA
{
	vec2 tc;
} fs_in;

uniform sampler2D tex;
uniform vec4 absoluteColor;

void main()
{
	vec4 texColor = texture(tex, fs_in.tc);
	if (texColor.w == 0.0)
		discard;
	if (absoluteColor.x == -1.0)
		color = texColor;
	else {
		color = absoluteColor;
		color.w = absoluteColor.w * texColor.w;
	}
	//color = vec4(0.5, 0, 0.5, 0.7);
}