import { render } from "@testing-library/react";
import CanvasDraw from "react-canvas-draw";

export default function Tile({ borderColor, color, symbol, size }) {
  return (
    <div
      style={{
        height: size,
        width: size,
        margin: 1,
        backgroundColor: borderColor,
        border: "1px solid rgba(0, 0, 0, 0)",
      }}
    >
      <div
        style={{
          height: size,
          width: size,
          backgroundColor: color,
          justifyContent: 'center',
          alignContent: 'center'
        }}
      >
        <b>{symbol}</b>
      </div>
    </div>
  );
}
