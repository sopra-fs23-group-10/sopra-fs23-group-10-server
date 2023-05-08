|           | **DevTask**                                                                                                                                  | **Test**                                                                                                                                                 |
|-----------|----------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------|
| &#9745;   | Profiles registered by the user get checked and saved in the backend #34                                                                     | createUser_duplicateUsername_throwsException (UserServiceIntegrationTest)                                                                                |
| &#9745;   | Logged-in users can log out and log back into their registered profile #38                                                                   | login_whenValid_thenReturnUserWithToken_200 (UserControllerTest)                                                                                         |
| &#9745;   | Handle the logout request in the backend #40                                                                                                 | checkDoLogout_success (UserServiceIntegrationTest)                                                                                                       |
| &#9745;   | Make username, global rank and profile picture of all online users retrievable in backend #44                                                | getOnlineUsers_allValid_thenReturnsOnlineUsers_200 (UserControllerTest)                                                                                  |
| &#9745;   | The text question is retrieved by the The Trivia API #45                                                                                     | createQuestion_thenQuestionCreated_201 (GameControllerTest)                                                                                              |
| &#9745;   | The answer is received in the backend and saved #57                                                                                          | answerQuestion_whenQuestionNotAnswered_thenSuccess_201 (GameControllerTest)                                                                              |
| &#9744;   | A round ends when both players have answered, or the timer has run out #58                                                                   | - was this implemented?                                                                                                                                  |
| &#9745;   | Make it possible to retrieve the usernames, profile pictures and the points achieved by the players in the current game from the backend #60 | intermediateGame_whenValid_returnResults_200 (GameControllerTest)                                                                                        |
| &#9745;   | The scores and awarded points are handled in the backend accordingly #64                                                                     | Implicitly tested by intermediateGame_whenValid_returnResults_200 (GameContorllerTest)                                                                   |
| &#9745;   | The performance of a user is measured by correctness of the answer and response speed #65                                                    | Implicitly tested by intermediateGame_whenValid_returnResults_200 (GameControllerTest)                                                                   |
| &#9744;   | The points are accumulated over all games and stored permanently #66                                                                         |                                                                                                                                                          |
| &#9745;   | The topics to choose from are randomly selected from a variety of available topics #68                                                       | getRandomTopics_once_success (GameControllerServiceTest)                                                                                                 |
| commented | Questions are retrieved through the backend according to the selected topic #69                                                              | getQuestion_validInput_success (GameControllerServiceTest)                                                                                               |
| commented | The turn to select the topic is evenly distributed between both players #70                                                                  | getRandomTopics_requestedTwice_throwsException (GameControllerServiceTest)                                                                               |
| &#9745;   | The challenged player gets to choose the topic first at the beginning of a match #71                                                         | getRandomTopics_rotating_success (GameControllerServiceTest)                                                                                             |
| &#9745;   | If a game gets cancelled, said cancelled game is also terminated in the backend #73                                                          | removeGame_validInput_success (GameControllerServiceTest)                                                                                                |
| &#9744;   | The invitation is sent to the invited user via the backend #74                                                                               |                                                                                                                                                          |
| &#9744;   | Both users are informed about the answer of the invited user #75                                                                             |                                                                                                                                                          |
| &#9744;   | the database is hosted on google, persistent and set up with protected Github secrets #77                                                    | how do we test this?                                                                                                                                     |
| &#9745;   | Handle the put request for changing profile picture an username in the backend accordingly. #80                                              | changeUsername_validInput_success (UserServiceTest)                                                                                                      |
| &#9745;   | The user list can only be retrieved from the backend by validated clients #84                                                                | getOnlineUsers_invalidToken_thenThrowsUnauthorized_401 (UserControllerTest)                                                                              |
| &#9745;   | Assign a default profile picture in the backend. #85                                                                                         | createUser_validInput_success (userServiceIntegrationTest)                                                                                               |
| &#9745;   | The answer received during single mode are stored in the backend. #94                                                                        | answerQuestion_whenQuestionNotAnswered_thenSuccess_201 (GameControllerTest), intermediateResults_OnlyOneUserAnswered_success (GameControllerSerivceTest) | 
| &#9744;   | In Single Mode, the game progresses in the backend when the player has answered or the timer has run out #95                                 |                                                                                                                                                          |
| &#9745;   | Make intermediate scores retrievable from the backend during Single Mode #98                                                                 | intermediateGame_whenValid_returnResults_200 (GameControllerTest)                                                                                        |
| &#9744;   | In Duel Mode, inform the client if they have answered faster from the backend #99                                                            |                                                                                                                                                          |
| &#9745;   | In Duel Mode, award the faster user more points than the slower one in the backend (if the answer was correct) #100                          | intermediatePoints_timeDifference_fasterGetsMorePoints (GameControllerServiceTest)                                                                       |
| &#9745;   | Make a list of all topics retrievable from the backend #105                                                                                  | getAllTopics_success (GameControllerServiceTest)                                                                                                         |
| &#9745;   | Make the detailed score information retrievable from the backend, so they can be displayed in the client at the end of a game #108           | getEndResult_validInput_success (GameControllerServiceTest)                                                                                              |
| &#9745;   | Implement the necessary functionalities for a rematch in the backend. #112                                                                   | deleteGame_success() (GameControllerTest)                                                                                                                |
| &#9745;   | Make sure every question in a game is unique #113                                                                                            | createQuestion_returnsQuestionDTOW_LoopTwiceBecauseAlreadyExistent()                                                                                     |
| &#9745;   | return body of answerQuestion via REST returns the correct answer #115                                                                       | answerQuestion_whenQuestionNotAnswered_thenSuccess_201()                                                                                                 |
| &#9745;   | Make ranking retrievable from the backend #118                                                                                               | updateRank_success()  (UserServiceTest)                                                                                                                  |