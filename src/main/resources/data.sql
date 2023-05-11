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
  (6, 'Brown Bear,Racoon,Bigfoot,Ape', 'RLcpM4K', 'Brown Bear', 'Ape,Racoon,Bigfoot', 'What animal is hidden in the image?'),
  (7, 'Alligator,Car,Skyline,Dinosaur', 'UBDjXWB', 'Alligator', 'Car,Skyline,Dinosaur', 'What object is hidden in the image?'),
  (8, 'Mars,Jupyter,Neptune,Venus', 'kfjHRvR', 'Mars', 'Venus,Jupyter,Neptune', 'What planet is hidden in the image?'),
  (9, 'Bird,Chipmunk,Rabbit,Weasel', 'oA2709v', 'Bird', 'Chipmunk,Rabbit,Weasel', 'What animal is hidden in the image?'),
  (10, 'Tokyo,Paris,London,Singapore', 'zV0qTkp', 'Tokyo', 'Paris,London,Singapore', 'What city is hidden in the image?'),
  (11, 'Kilimanjaro,Everest,Matterhorn,Mont Blanc', 'V2DBESE', 'Kilimanjaro', 'Everest,Matterhorn,Mont Blanc', 'What mountain is hidden in the image?'),
  (12, 'Dog,Cat,Donkey,Goat', 'AAJc1Rh', 'Donkey', 'Cat,Dog,Goat', 'What kind of animal is hidden in the image?'),
  (13, 'Space Rocket,Candle,Firework,Cigarette', 'hChUSOF', 'Space Rocket', 'Candle,Firework,Cigarette', 'What object is hidden in the image?'),
  (14, 'Beetle,Rhinoceros,Dinosaur,Spider', 'WtgPUUQ', 'Beetle', 'Rhinoceros,Dinosaur,Spider', 'What kind of animal is hidden in the image?'),
  (15, 'Our Sun,Human Cell,Frog Egg,Tennis Ball', 'B3QJiiA', 'Sun', 'Human Cell,Frog Egg,Tennis Ball', 'What object is hidden in the macro image?'),
  (16, 'Bud Spencer & Terrence Hill,Dumb and Dumber,Laurel & Hardy,Cheeck & Chong', '3XXSb62', 'Bud Spencer & Terrence Hill', 'Dumb and Dumber,Laurel & Hardy,Cheeck & Chong', 'Who are the legends in the photograph?'),
  (17, 'Albert Einstein,Hendrik Lorentz,Max Planck,Niels Bohr', 'YrBRqnp', 'Albert Einstein', 'Hendrik Lorentz,Max Planck,Niels Bohr', 'Who is the guy making fun of the paparazzi?'),
  (18, 'Black Hole,Galaxy,Nebula,Star', 'TzZtugJ', 'Black Hole', 'Galaxy,Nebula,Star', 'What object was captured in this awesome image?'),
  (19, 'Rowan Atkinson,George Washington,Louis XIV,Edward the Great', 'il6w7zw', 'Rowan Atkinson', 'George Washington,Louis XIV,Edward the Great', 'Which famous person shows her face in the painting?');;