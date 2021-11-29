// ================================================
// JS 按照【姚尊】算法，实现 🔥火焰🔥 效果
// 学问真的很多很多，感谢【姚大大】的支持！！
// ================================================

/**
 * https://docs.gl/sl4/mod
 */
function mod(a, b) {
  return a - b * Math.floor(a / b);
}

/**
 * https://docs.gl/sl4/step
 */
function step(a, b) {
  return a < b ? 0 : 1;
}

/**
 * https://developer.download.nvidia.com/cg/saturate.html
 * 
 * float saturate(float x) {
 *    return max(0, min(1, x));
 * }
 */
function saturate(x) {
  return Math.max(0, Math.min(1, x));
}

/**
 * https://developer.download.nvidia.com/cg/smoothstep.html
 * 
 * float smoothstep(float a, float b, float x) {
 *    float t = saturate((x - a)/(b - a));
 *    return t*t*(3.0 - (2.0*t));
 * }
 */
function smoothstep(a, b, x) {
  const t = saturate((x - a) / (b - a));
  return t * t * (3.0 - 2.0 * t);
}

function play(callback, { fps = 60 } = {}) {

  let start = 0;
  const t = 1000 / fps;

  const playAni = () => {
    // https://developer.mozilla.org/zh-CN/docs/Web/API/Window/requestAnimationFrame
    // https://developer.mozilla.org/zh-CN/docs/Web/API/DOMHighResTimeStamp
    requestAnimationFrame((DOMHighResTimeStamp) => {
      if (DOMHighResTimeStamp - start >= t) {
        start = DOMHighResTimeStamp;
  
        callback();
      }
     
      playAni();
    })
  }

  playAni();
}

const burnColorOut = {
  r: 235,
  g: 23,
  b: 23,
};

const burnColorIn = {
  r: 220,
  g: 113,
  b: 22,
};

export {
  mod,
  step,
  saturate,
  smoothstep,
  play,
  burnColorOut,
  burnColorIn
}