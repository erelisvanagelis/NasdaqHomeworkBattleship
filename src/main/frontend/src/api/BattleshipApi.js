const axios = require("axios").default;
const baseurl = "/battleship/api";

export async function createGame(gameId) {
  try {
    const response = await axios.post(`${baseurl}/${gameId}`);
    console.log(response);
    return { success: true, data: { ...response.data } };
  } catch (error) {
    console.error(error);
    const message = error.response.data;
    return { success: false, data: message };
  }
}

export async function getCurrentState(gameId) {
  try {
    const response = await axios.get(`${baseurl}/${gameId}`);
    console.log(response);
    return { success: true, data: { ...response.data } };
  } catch (error) {
    console.error(error);
    const message = error.response.data;
    return { success: false, data: message };
  }
}

export async function resetGame(gameId) {
  try {
    const response = await axios.delete(`${baseurl}/${gameId}/reset`);
    console.log(response);
    return { success: true, data: { ...response.data } };
  } catch (error) {
    console.error(error);
    const message = error.response.data;
    return { success: false, data: message };
  }
}

export async function performAttack(gameId, player, letter, number) {
  try {
    const response = await axios.post(`${baseurl}`, {
      gameId: gameId,
      player: player,
      coordinates: {
        letter: letter,
        number: number,
      },
    });

    console.log(response);
    return { success: true, data: response.data };
  } catch (error) {
    console.error(error);
    const message = error.response.data;
    return { success: false, data: message };
  }
}
