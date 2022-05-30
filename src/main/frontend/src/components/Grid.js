import { Stack } from "@mui/material";
import Tile from "./Tile";

export default function Grid({ player, tiles, selectedPlayer }) {
  let previousLetter = null;
  const borderColor = "grey";
  const hit = "red";
  const miss = "blue";
  const notShotWater = "grey";
  const notShotShip = "green";
  console.log(`${player} ${selectedPlayer} ${player === selectedPlayer}`)

  return (
    <div style={{ height: "300px", width: "340px" }}>
      <h1>PLAYER {player}</h1>
      <Stack direction="row" flexWrap="wrap">
        {tiles.map((tile) => {
          if (tile.shot) {
            if (tile.tag === "WATER")
              return (
                <Tile borderColor={borderColor} color={miss} size="30px" />
              );
            else
              return <Tile borderColor={borderColor} color={hit} size="30px" />;
          } else if (player === selectedPlayer) {
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
                symbol={`${tile.coordinates.letter}${tile.coordinates.number}`}                
              />
            );
        })}
      </Stack>
    </div>
  );
}
