INSERT INTO player (
  id,
  background_music,
  email,
  password,
  points,
  profile_picture,
  status,
  token,
  username,
  rank
  )

VALUES (1, false, 'singleton@singleton.ch', 'lsd2jf$-q5wfead$nsk234r$f4hasd', 0, 'singleton', 0, 'ee763d2b-b89b-426b-9e36-72b995bef822', 'singletonUser',  9999);

INSERT INTO image_question (
  template_image_question_id,
  all_answers,
  api_id,
  correct_answer,
  incorrect_answers,
  question
)

VALUES
  (2, 'Cat,Mouse,Hamster,Dog', 'sjpa0Gg', 'Dog', 'Cat,Mouse,Hamster', 'What kind of animal do you see?'),
  (3, 'Motorcycle,Car,Bicycle,Trike', 'jBtFMql', 'Motorcycle', 'Car,Bicycle,Trike', 'What kind of vehicle do you see?'),
  (4, 'Ferrari,Lamborghini,Aston Martin,Bentley', 'Q1rBT7z', 'Ferrari', 'Lamborghini,Aston Martin,Bentley', 'What kind of car do you see?'),
  (5, 'Beach,Cave,Forest,Harbor', 'Ho2Ni4I', 'Beach', 'Cave,Forest,Harbor', 'Where are you at?');