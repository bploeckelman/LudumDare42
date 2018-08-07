#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_accum;

varying vec4 v_color;
varying vec2 v_texCoord;

void main() {

    vec2 translatedCoord = vec2(v_texCoord.x, v_texCoord.y + sin(v_texCoord.x + v_texCoord.y + u_accum)/8.);
    vec4 color = texture2D(u_texture, translatedCoord);
    gl_FragColor = color;
}