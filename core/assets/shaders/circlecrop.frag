#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform sampler2D u_texture1;
uniform float u_percent;

varying vec4 v_color;
varying vec2 v_texCoord;

const vec4 bgcolor = vec4(0.0, 0.0, 0.0, 1.0);



vec4 getFromColor(vec2 p){
   return texture2D(u_texture1, p);
}

vec4 getToColor(vec2 p){
   return texture2D(u_texture, p);
}


vec4 transition(vec2 p) {

    vec2 ratio2 = vec2(1.0, 1.0);
    float s = pow(2.0 * abs(u_percent - 0.5), 3.0);

  float dist = length((vec2(p) - 0.5) * ratio2);
  return mix(
    u_percent < 0.5 ? getFromColor(p) : getToColor(p), // branching is ok here as we statically depend on progress uniform (branching won't change over pixels)
    bgcolor,
    step(s, dist)
  );
}

void main() {

      vec2 flippedCoord = vec2(v_texCoord.x, 1. - v_texCoord.y);

        gl_FragColor = transition(flippedCoord);
}