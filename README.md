<img src="readme_images/BrainBusters.png" height="100"/>

## Table of Content

- [Introduction](#introduction)
- [Built With](#built-with)
- [Components](#main-components)
- [Local Development](#deploy-locally)
- [Deployment](#deployment)
- [Roadmap](#roadmap)
- [Authors & Acknowledgments](#authors--acknowledgments)
- [License](#license)

## Introduction

Brain Busters is a trivia game designed to entertain and educate players on a wide range of topics, from history and geography to pop culture and sports. It provides a solution to the problem of limited entertainment and educational options for people seeking to learn, have fun, connect with others, and maybe even discover new interests.  

The game stands out for its various game modes. The classic trivia game offers a traditional style of gameplay, while the image mode gradually reveals blurry pictures. Both modes challenge players on quick decision-making. The facts are accessed and updated using an API, keeping the game up-to-date and accurate.  

Next to the single mode, the tournament mode adds an exciting element of competition, allowing players to participate in a bracket-style tournament, creating a more dynamic and engaging experience, as players can track their progress and compete for the ultimate title of Brain Busters champion.

## Built With

* [React](https://react.dev/) - Front-end JavaScript library
* [Spring](https://spring.io/projects/spring-framework) - Framework that enables running JVM
* [Gradle](https://gradle.org/) - Build automation tool
* [STOMP](https://stomp-js.github.io/stomp-websocket/) - Bidirectional real time communication
* [The Trivia API](https://the-trivia-api.com/) - REAST API for retreiving text questions
* [Mailjet API](https://www.mailjet.com/) - REST API for sending mails
* [PostgreSQL](https://www.postgresql.org/) - Persistent Open Source DB

## Main Components

#### User
The [User](https://github.com/sopra-fs23-group-10/sopra-fs23-group-10-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/entity/User.java)
entity is a core part of BrainBuster, since else in order to participate in a game one has to be able to log in, log out and getting their achieved points.
Additionally, an important factor is that one gets recognized and is able to keep the points in their game und thus being able to compare their rank/effort with other users.

#### UserController
The [UserController](https://github.com/sopra-fs23-group-10/sopra-fs23-group-10-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/controller/UserController.java)
handles requests from the front-end regarding users and fulfills their requests.

#### Game
The [Game](https://github.com/sopra-fs23-group-10/sopra-fs23-group-10-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/entity/Game.java)
entity is a crucial part of BainBusters since without a game the users would not be able to play and obtain their points.
Nor being able to retrieve the appropriate questions.

#### GameController
The [GameController](https://github.com/sopra-fs23-group-10/sopra-fs23-group-10-server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs23/controller/GameController.java)
responds to requests from the front-end regarding Games and fulfills their requests.

#### Websockets
The [Websockets](https://github.com/sopra-fs23-group-10/sopra-fs23-group-10-server/tree/main/src/main/java/ch/uzh/ifi/hase/soprafs23/websockets)
enables to directly inform the client from the server.

## Deploy Locally with Gradle
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See [deployment](#deployment) for notes on how to deploy the project on a live system.

#### Clone Repository
Clone the client-repository onto your local machine with the help of [Git](https://git-scm.com/downloads).
```bash 
git clone https://github.com/sopra-fs23-group-10/sopra-fs23-group-10-server.git
```
#### Build
```bash 
./gradlew build
```

#### Run
The Spring Boot profile for a local H2-DB is used for non-persistent DB
Server should be up and running on `localhost:8080`.
```bash 
./gradlew bootRun -DSPRING_PROFILES_ACTIVE=test
```

#### Test
```bash 
./gradlew test
```

## Deployment
- the main branch is automatically mirrored onto Google Cloud App Engine via GitHub workflow
- Google SQL PostgreSQL instance is hosted on Google Cloud
- Credentials for Google App Engine and SQL instances are replaced with GitHub Secrets

### Create Releases
- [follow GitHub documentation](https://docs.github.com/en/repositories/releasing-projects-on-github/managing-releases-in-a-repository)
- database is reset during a release with the current settings!

## Roadmap
Features that new developers who want to contribute to our project could add.
- new Game mode music quiz.<br>
- convert to iOS & Android native app
- increase responsiveness for mobile browsers
- add new records to our image database

## Authors & Acknowledgments

### Authors
* **Sarah Egger** - *Frontend* - [saeie07](https://github.com/saeie07)
* **Linn Spitz** - *Frontend* - [ringdinglinn](https://github.com/ringdinglinn)
* **Patrick Widmer** - *Backend* - [Chopstew](https://github.com/Chopstew)
* **Sandrin Hunkeler** - *Backend* - [Altishofer](https://github.com/Altishofer)
* **Cédric Lüchinger** - *Backend* - [dattes](https://github.com/dattes)

### Acknowledgments
We would like to thank our tutor [jemaie](https://github.com/jemaie) and the whole team of the course Software Engineering Lab from the University of Zurich.

## License
This project is licensed under the Apache License 2.0 - see the [LICENSE](https://github.com/sopra-fs23-group-10/sopra-fs23-group-10-server/blob/main/LICENSE) file for details.
