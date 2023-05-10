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

VALUES (0, false, 'singleton@singleton.ch', 'lsd2jf$-q5wfead$nsk234r$f4hasd', 0, 'singleton', 0, 'ee763d2b-b89b-426b-9e36-72b995bef822', 'singletonUser',  9999);

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
  (5, 'Beach,Cave,Forest,Harbor', 'Ho2Ni4I', 'Beach', 'Cave,Forest,Harbor', 'Where are you at?'),
  (6, 'Brown Bear, Racoon, Bigfoot, Ape', 'RLcpM4K', 'Ape', 'Brown Bear, Racoon, Bigfoot', 'What animal is hidden in the image?'),
  (7, 'Alligator, Car, Skyline, Dinosaur', 'UBDjXWB', 'Alligator', 'Car, Skyline, Dinosaur', 'What object is hidden in the image?'),
  (8, 'Mars, Jupyter, Neptune, Venus', 'gQJGMqm', 'Venus', 'Mars, Jupyter, Neptune', 'What planet is hidden in the image?'),
  (9, 'Bird, Chipmunk, Rabbit, Weasel', 'oA2709v', 'Bird', 'Chipmunk, Rabbit, Weasel', 'What animal is hidden in the image?'),
  (10, 'Tokyo, Paris, London, Singapore', 'zV0qTkp', 'Tokyo', 'Paris, London, Singapore', 'What city is hidden in the image?'),
  (11, 'Kilimanjaro, Everest, Matterhorn, Mont Blanc', 'V2DBESE', 'Kilimanjaro', 'Everest, Matterhorn, Mont Blanc', 'What mountain is hidden in the image?'),
  (12, 'Dog, Cat, Donkey, Goat', 'AAJc1Rh', 'Dog', 'Cat, Donkey, Goat', 'What kind of animal is hidden in the image?'),
  (13, 'Space Rocket, Candle, Firework, Cigarette', 'hChUSOF', 'Space Rocket', 'Candle, Firework, Cigarette', 'What object is hidden in the image?'),
  (14, 'Beetle, Rhinoceros, Dinosaur, Spider', 'WtgPUUQ', 'Beetle', 'Rhinoceros, Dinosaur, Spider', 'What kind of animal is hidden in the image?');