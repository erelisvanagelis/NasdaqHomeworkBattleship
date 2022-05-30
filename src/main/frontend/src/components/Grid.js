import { Stack } from "@mui/material";
import Tile from "./Tile";

export default function Grid({ title, tiles, showOnlyShot }) {
  let previousLetter = null;
  const borderColor = "grey";
  const hit = "red";
  const miss = "blue";
  const notShotWater = "grey";
  const notShotShip = "green";

  function selectTile(tile) {
    return;
  }

  return (
    <div style={{ height: "300px", width: "340px" }}>
      <h1>{title}</h1>
      <Stack direction="row" flexWrap="wrap">
        {tiles.map((tile) => {
          if (tile.shot) {
            if (tile.tag === "WATER")
              return (
                <Tile borderColor={borderColor} color={miss} size="30px" />
              );
            else
              return <Tile borderColor={borderColor} color={hit} size="30px" />;
          } else if (!showOnlyShot) {
            if (tile.tag === "WATER")
              return (
                <Tile
                  borderColor={borderColor}
                  color={notShotWater}
                  size="30px"
                />
              );
            else
              return (
                <Tile
                  borderColor={borderColor}
                  color={notShotShip}
                  size="30px"
                />
              );
          } else
            return (
              <Tile
                borderColor={borderColor}
                color={notShotWater}
                size="30px"
              />
            );
        })}
      </Stack>
    </div>
  );
}
