import { step, smoothstep, burnColorOut, burnColorIn, play } from "./utils.js";

// ==================== 火焰 canvas =============================

const canvasDom = document.getElementById("fire");
const ctx = canvasDom.getContext("2d");

const canvasWidth = canvasDom.width;
const canvasHeight = canvasDom.height;

// ==================== 加载噪点图 =============================

const noiseDom = document.getElementById("noise");
const noiseCtx = noiseDom.getContext("2d");
const noiseWidth = noiseDom.width;
const noiseHeight = 1024;

let noiseImage = new Image();
noiseImage.src = "./src/assets/noise.jpg";
noiseImage.crossOrigin = "Anonymous";
console.log('noiseImage = ', noiseImage);

// ==================== 加载成功后，运行 =============================

noiseImage.onload = function () {
  // 先把噪点图画到canvas上
  // 因为要火焰动画循环，所以画了两层，方便取像素点
  noiseCtx.drawImage(noiseImage, 0, 0, noiseWidth, noiseHeight);
  noiseCtx.drawImage(noiseImage, 0, noiseHeight, noiseWidth, noiseHeight);

  // 坐标
  let x = 0;
  let y = 0;

  // 取样范围的宽高
  let w = noiseWidth;
  let h = canvasHeight;

  function run() {
    // 取样
    let imageData = noiseCtx.getImageData(x, y, w, h);
    const data = imageData.data;

    for (let i = 0; i < data.length; i += 4) {
      let [r, g, b, a] = [data[i], data[i + 1], data[i + 2], data[i + 3]];

      const noiseValue = r / 255;

      // 当前像素点所处的火焰的高度的百分比
      // i 是所有的像素点的当前的位置，除于 w * 4，表示单个像素点行的总数
      const yPos = i / (w * 4);
      const p1 = yPos / h;

      const f1 = step(noiseValue, p1);
      const f2 = smoothstep(noiseValue, p1, p1 - 0.15);

      const r1 = f1 - f2;
      const r2 = f2;

      // 设置火焰为红色
      data[i] = 200 + burnColorOut.r * r1 + burnColorIn.r * r2;
      data[i + 1] = burnColorOut.g * r1 + burnColorIn.g * r2;
      data[i + 2] = burnColorOut.b * r1 + burnColorIn.b * r2;

      // 【重点】判断是否要显示这个红点(越亮就越不显示)
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

  play(run, {
    fps: 60
  });
};
