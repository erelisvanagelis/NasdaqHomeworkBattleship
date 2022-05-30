import "./App.css";
import { useEffect, useState } from "react";
import {
  Alert,
  Box,
  Button,
  Container,
  Divider,
  Input,
  Stack,
  Switch,
  TextField,
  ToggleButton,
  ToggleButtonGroup,
} from "@mui/material";
import * as BattleshipApi from "./api/BattleshipApi";
import * as constants from "./util/Constants";
import Tile from "./components/Tile";
import Grid from "./components/Grid";

function App() {
  const [gameId, setGameId] = useState("");
  const [messages, setMessages] = useState([]);
  const [selectedPlayer, setSelectedPlayer] = useState(constants.PLAYER_ONE);
  const [state, setState] = useState({
    turn: 0,
    playerTurn: constants.PLAYER_ONE,
    fpGrid: [],
    spGrid: [],
  });

  const [coordinates, setCoordinates] = useState({
    letter: "",
    number: "",
  });

  const handleAction = async (id) => {
    console.log(`actionId: ${id}`);
    let response;
    switch (id) {
      case constants.ACTION_CREATE_GAME:
        response = await BattleshipApi.createGame(gameId);
        if (response.success) {
          setMessages(["Game Created", ...messages]);
        } else {
          alert(response.data);
        }
        break;

      case constants.ACTION_GET_CURRENT_GAMESTATE:
        response = await BattleshipApi.getCurrentState(gameId);
        if (response.success) {
          setMessages([
            `Turn - ${response.data.turn} - ${response.data.action}`,
            ...messages,
          ]);
          setState({ ...response.data });
          console.log(state);
        } else {
          alert(response.data);
        }
        break;

      case constants.ACTION_RESET_GAME:
        response = await BattleshipApi.resetGame(gameId);
        if (response.success) {
          setMessages(["Game Reset", ...messages]);
        } else {
          alert(response.data);
        }
        break;

      case constants.ACTION_ATTACK:
        response = await BattleshipApi.performAttack(
          gameId,
          selectedPlayer,
          coordinates.letter,
          coordinates.number
        );
        if (response.success) {
          setMessages([response.data, ...messages]);
        } else {
          alert(response.data);
        }
        break;
    }
  };

  const handleAlignment = (event, value) => {
    if (value !== null) {
      setSelectedPlayer(value);
    }
  };

  return (
    <div className="App">
      <Container
        maxWidth="lg"
        sx={{ backgroundColor: "#778899", minHeight: "100vh" }}
      >
        <Stack alignItems="center" spacing={2}>
          <strong>BATTLESHIP</strong>

          <Stack direction="row" spacing={2}>
            <TextField
              label="Game Id"
              defaultValue={"Your game id"}
              value={gameId}
              onChange={(e) => setGameId(e.target.value)}
            />

            <Button
              variant="contained"
              onClick={() => handleAction(constants.ACTION_CREATE_GAME)}
            >
              Generate Game
            </Button>

            <Button
              variant="contained"
              onClick={() =>
                handleAction(constants.ACTION_GET_CURRENT_GAMESTATE)
              }
            >
              Retrieve Game
            </Button>

            <Button
              variant="contained"
              onClick={() => handleAction(constants.ACTION_RESET_GAME)}
            >
              Reset Game
            </Button>
          </Stack>



          <Stack direction="row" spacing={2}>         
            <TextField
              label="Letter"
              defaultValue={"Attack Letter"}
              value={coordinates.letter}
              onChange={(e) =>
                setCoordinates({ ...coordinates, letter: e.target.value })
              }
            />

            <TextField
              label="Number"
              defaultValue={"Attack Number"}
              value={coordinates.number}
              onChange={(e) =>
                setCoordinates({ ...coordinates, number: e.target.value })
              }
            />

          <ToggleButtonGroup
            value={selectedPlayer}
            exclusive
            onChange={handleAlignment}
          >
            <ToggleButton value={constants.PLAYER_ONE}>
              PLAYER {constants.PLAYER_ONE}
            </ToggleButton>
            <ToggleButton value={constants.PLAYER_TWO}>
              PLAYER {constants.PLAYER_TWO}
            </ToggleButton>
          </ToggleButtonGroup>            

            <Button
              variant="contained"
              onClick={() => handleAction(constants.ACTION_ATTACK)}
            >
              Attack
            </Button>
          </Stack>
          

          <Stack direction="row" spacing={2}>
            <Grid
              title="Player One"
              tiles={state.fpGrid}
              showOnlyShot={false}
            />
            <Divider orientation="vertical" flexItem />
            <Grid title="Player Two" tiles={state.spGrid} showOnlyShot={true} />
            <Divider orientation="vertical" flexItem />
            <ul>
              {messages.map((message) => (
                <li>{message}</li>
              ))}
            </ul>
          </Stack>
        </Stack>
      </Container>
    </div>
  );
}

export default App;
