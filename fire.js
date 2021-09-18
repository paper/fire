const canvasDom = document.getElementById("canvas");
const ctx = canvasDom.getContext("2d");

const canvasWidth = canvasDom.width;
const canvasHeight = canvasDom.height;

// ==================== 加载噪点图 =============================

const noiseDom = document.getElementById("noise");
const noiseCtx = noiseDom.getContext("2d");
const noiseWidth = noiseDom.width;
const noiseHeight = 1024;
let image = new Image();
image.src = "./noise.png";
image.crossOrigin = "Anonymous";

// ================================================
// JS 按照【姚尊】算法，实现 🔥火焰🔥 效果
// 学问真的很多很多，感谢【姚大大】的支持！！
// ================================================

// https://docs.gl/sl4/mod
function mod(a, b) {
  return a - b * Math.floor(a / b);
}

// https://docs.gl/sl4/step
function step(a, b) {
  return a < b ? 0 : 1;
}

/**
 * https://developer.download.nvidia.com/cg/saturate.html
 * float saturate(float x)
 * {
 *    return max(0, min(1, x));
 * }
 */
function saturate(x) {
  return Math.max(0, Math.min(1, x));
}

/**
 * // https://developer.download.nvidia.com/cg/smoothstep.html
 * float smoothstep(float a, float b, float x)
 * {
 *    float t = saturate((x - a)/(b - a));
 *    return t*t*(3.0 - (2.0*t));
 * }
 */
function smoothstep(a, b, x) {
  const t = saturate((x - a) / (b - a));
  return t * t * (3.0 - 2.0 * t);
}

const fps = 60;

const burnColorOut = {
  r: 235,
  g: 23,
  b: 23
};

const burnColorIn = {
  r: 220,
  g: 113,
  b: 22
}

image.onload = function () {
  // 先把噪点图画到canvas上，方便取点
  noiseCtx.drawImage(image, 0, 0, noiseWidth, noiseHeight);
  noiseCtx.drawImage(image, 0, noiseHeight, noiseWidth, noiseHeight);

  // 坐标
  let x = 0;
  let y = 0;

  // 取样范围的宽高
  let w = noiseWidth;
  let h = canvasHeight;

  function run() {
    // 取样
    let imageData = noiseCtx.getImageData(x, y, w, h);
    data = imageData.data;

    for (let i = 0; i < data.length; i += 4) {
      let [r, g, b, a] = [data[i], data[i + 1], data[i + 2], data[i + 3]];

      const noiseValue = r / 255;

      // 当前像素点所处的火焰的高度的百分比
      // i 是所有的像素点的当前的位置，除于 w * 4，表示单个像素点行的总数
      const ypos = i / (w * 4);
      const p1 = ypos / h;

      const f1 = step(noiseValue, p1);
      const f2 = smoothstep(noiseValue, p1, p1 - 0.15);
      
      const r1 = f1 - f2;
      const r2 = f2;

      // 设置火焰为红色
      data[i] = 200 + burnColorOut.r * r1 + burnColorIn.r * r2;
      data[i + 1] = burnColorOut.g * r1 + burnColorIn.g * r2;
      data[i + 2] = burnColorOut.b * r1 + burnColorIn.b * r2;

      // 判断是否要显示这个红点(越亮就越不显示)
      if (f1) {
        data[i + 3] = 0;
      }
    }

    ctx.putImageData(imageData, 0, 0);

    y += 10;

    if (y >= noiseHeight) {
      y = 0;
    }
  }

  function animate() {
    setTimeout(() => {
      run();

      requestAnimationFrame(animate);
    }, 1000 / fps);
  }

  animate();

};
