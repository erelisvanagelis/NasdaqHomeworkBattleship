# REST Battleship api developed using SpringBoot.

## Battleship REST service
This service allows user to generate game boards, retrieve them and update them by performing attacks.

### About the project
###### Program is written in Kotlin programming language.
###### Framework used to implement REST service was Spring boot.
###### Frontend is written using React.js
###### All data was kept in memory.
###### Interactions with the application are REST endpoints and frontend application.
###### Final delivery is Maven project.

## Project MVC model

### Models
- GameState - Data class that holds data abbout a turn
- AttackRequest - Data class that is used to decide which GameState/Tile is attacked
- Coordinates - Data class that holds coordinates of a tile
- Tile - Data class that holds information about a tile

### Controller
BattleshipRESTController - Main controller that host api requests. 
BattleshipController - Controller used to host frontend.

### View
A basic react app that makes calls to the main API

## Building the project
You will need:
- Java JDK 17 or higher
- Maven 3.8.1 or higher

### Clone the project and use Maven to build the server
###

mvn clean install

### To run the project with frontend enabled

mvn spring-boot:run


## API Usage

