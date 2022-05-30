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

  const [importantMessage, setImportantMessage] = useState("");

  const handleMessages = (message) => {
    if (messages[messages.length - 1] !== message)
      setMessages([message, ...messages]);
  };

  const handleAction = async (id) => {
    console.log(`actionId: ${id}`);
    let response;
    switch (id) {
      case constants.ACTION_CREATE_GAME:
        response = await BattleshipApi.createGame(gameId);
        if (response.success) {
          handleMessages("Game Created");
        } else {
          alert(response.data);
        }
        break;

      case constants.ACTION_GET_CURRENT_GAMESTATE:
        response = await BattleshipApi.getCurrentState(gameId);
        if (response.success) {
          if (response.data.attackCoordinates == null) {
            handleMessages(
              `Turn - ${response.data.turn} - ${response.data.action}`
            );

            if (response.data.playerTurn === constants.PLAYER_ONE) {
              setImportantMessage(`PLAYER ${constants.PLAYER_ONE} TURN`);
            } else {
              setImportantMessage(`PLAYER ${constants.PLAYER_TWO} TURN`);
            }

            if (response.data.gameOver == true) {
              setImportantMessage(`GAME OVER`);
            }
          } else {
            handleMessages(
              `Turn - ${response.data.turn} - ${response.data.action} - ${response.data.attackCoordinates.letter}${response.data.attackCoordinates.number}`
            );
          }

          setState({ ...response.data });
          console.log(state);
        } else {
          alert(response.data);
        }
        break;

      case constants.ACTION_RESET_GAME:
        response = await BattleshipApi.resetGame(gameId);
        if (response.success) {
          handleMessages("Game Reset");
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
          handleMessages(response.data);
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
      <Stack
        alignItems="start"
        justifyContent="center"
        direction="row"
        spacing={2}
        sx={{ backgroundColor: "#778899", minHeight: "100vh" }}
      >
        <Stack spacing={2} justifyContent="center" alignContent="center" sx={{ minHeight: "100vh" }}>
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
            onClick={() => handleAction(constants.ACTION_GET_CURRENT_GAMESTATE)}
          >
            Retrieve Game
          </Button>

          <Button
            variant="contained"
            onClick={() => handleAction(constants.ACTION_RESET_GAME)}
          >
            Reset Game
          </Button>

          <TextField
            label="Letter"
            defaultValue={"Attack Letter"}
            value={coordinates.letter}
            onChange={(e) =>
              setCoordinates({
                ...coordinates,
                letter: e.target.value.toUpperCase(),
              })
            }
          />

          <TextField
            label="Number"
            defaultValue={"Attack Number"}
            value={coordinates.number}
            onChange={(e) => {
              const maybeNumber = parseInt(e.target.value);
              if (Number.isInteger(maybeNumber))
                setCoordinates({ ...coordinates, number: maybeNumber });
            }}
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
        <Divider orientation="vertical" flexItem />
        <Stack spacing={2} alignItems="center" justifyContent="space-around">
          <h1>BATTLESHIP</h1>
          <strong>{importantMessage}</strong>
          <Stack spacing={2} direction="row">
            <Grid
              player={constants.PLAYER_ONE}
              tiles={state.fpGrid}
              selectedPlayer={selectedPlayer}
            />
            <Divider orientation="vertical" flexItem />
            <Grid
              player={constants.PLAYER_TWO}
              tiles={state.spGrid}
              selectedPlayer={selectedPlayer}
            />
          </Stack>
        </Stack>
        <Divider orientation="vertical" flexItem />
        <ul>
          {messages.map((message) => (
            <li>{message}</li>
          ))}
        </ul>
      </Stack>
    </div>
  );
}

export default App;
