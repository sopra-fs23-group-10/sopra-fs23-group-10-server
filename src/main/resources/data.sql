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
  (15, 'Sun,Human Cell,Frog Egg,Tennis Ball', 'B3QJiiA', 'Sun', 'Human Cell,Frog Egg,Tennis Ball', 'What object is hidden in the image?'),
  (16, 'Bud Spencer & Terrence Hill,Dumb and Dumber,Laurel & Hardy,Cheeck & Chong', '3XXSb62', 'Bud Spencer & Terrence Hill', 'Dumb and Dumber,Laurel & Hardy,Cheeck & Chong', 'Who are the legends in the photograph?'),
  (17, 'Albert Einstein,Hendrik Lorentz,Max Planck,Niels Bohr', 'YrBRqnp', 'Albert Einstein', 'Hendrik Lorentz,Max Planck,Niels Bohr', 'Who is the guy making fun of the paparazzi?'),
  (18, 'Black Hole,Galaxy,Nebula,Star', 'TzZtugJ', 'Black Hole', 'Galaxy,Nebula,Star', 'What object was captured in this awesome image?'),
  (19, 'Rowan Atkinson,George Washington,Louis XIV,Edward the Great', 'il6w7zw', 'Rowan Atkinson', 'George Washington,Louis XIV,Edward the Great', 'Which famous person shows his face in the painting?'),
  (20, 'Fox,Sea lion,Cat,Dog', 'amckVul', 'Cat', 'Fox,Sea lion,Dog', 'Who is the impostor?'),
  (21, 'Bug,Spider,Butterfly,Seahorse', 'Sm1f3Np', 'Seahorse', 'Bug,Spider,Butterfly', 'What animal is hidden in the image?'),
  (22, 'Butterfly,Plane,Sparrow,Hummingbird', 'XbS348E', 'Hummingbird', 'Butterfly,Plane,Sparrow', 'What object was captured in this image?'),
  (23, 'Florida,Munich,Lisbon,Cape Town', 'qGzjZfD', 'Cape Town', 'Florida,Munich,Lisbon', 'What city is hidden in the image?'),
  (24, 'Jack Nicholson,Sean Penn,Clint Eastwood,John Lennon', '2I2KdXG', 'John Lennon', 'Jack Nicholson,Sean Penn,Clint Eastwood', 'Who is in this picture?'),
  (25, 'Dolphin,Flying Fish,Jet Ski,Humpback Whale', 'tUza1OK', 'Humpback Whale', 'Dolphin,Flying Fish,Jet Ski', 'Who is flying in this image?'),
  (26, 'Cabin Cruiser,Galley,Ferry,Bow Rider', '9JkHXb9', 'Bow Rider', 'Cabin Cruiser,Galley,Ferry', 'What kind of boat type is in the image?'),
  (27, 'Cabriolet,SUV,Limousine,Coupe', 'eAhl2LC', 'Coupe', 'Cabriolet,SUV,Limousine', 'What is the design of the car in the picture?'),
  (28, 'Spiderman,Thor,Ironman,Captain America', 'KEfW1tG', 'Captain America', 'Spiderman,Thor,Ironman', 'What marvel character is in the picture?'),
  (29, 'Friends,Modern Family,Suits,The Office', 'tphNbfC', 'The Office', 'Friends,Modern Family,Suits', 'From which TV series is this struggling guy?'),
  (30, 'New York,London,Zurich,Tokyo', 'AjybU6j', 'Tokyo', 'New York,London,Zurich', 'In which city is this picture taken?'),
  (31, 'Hong Kong Park,Park Guell,Djurgarden,Central Park', 'cvyMf0E', 'Central Park', 'Hong Kong Park,Park Guell,Djurgarden', 'How is this park called?'),
  (32, 'Antoni Gaudi,Frank Gehry,Le Corbusier,Frank Lloyd Wright', 'MNgvhvE', 'Frank Lloyd Wright', 'Antoni Gaudi,Frank Gehry,Le Corbusier', 'Who was the architect of this house?'),
  (33, 'Lake Geneva,Lake Como,Lake Zurich,Lake Constance', 'UGUP43s', 'Lake Constance', 'Lake Geneva,Lake Como,Lake Zurich', 'Which lake is in this picture?'),
  (34, 'Dog,Cheetah,Cat,Hyena', 'fQf86zn', 'Hyena', 'Dog,Cheetah,Cat', 'Who is looking at you?'),
  (35, 'Hamster,Mouse,Rat,Quokka', 'miLaIWb', 'Quokka', 'Hamster,Mouse,Rat', 'What kind of animal do you see in this awesome image?'),
  (36, 'Sea Lion,Walrus,Shark,Sea Cow', 'JaHt9Dz', 'Sea Cow', 'Sea Lion,Walrus,Shark', 'What kind of animal was captured in this awesome image?'),
  (37, 'Burj Al Arab,Taipei 101,Petronas Towers,Burj Khalifa', 'MLF8jrp', 'Burj Khalifa', 'Burj Al Arab,Taipei 101,Petronas Towers', 'What building was captured in this awesome image?'),
  (38, 'Summer,Spring,Autumn,Winter', 'S7kDnrA', 'Winter', 'Summer,Spring,Autumn', 'What season was captured in this awesome image?'),
  (40, 'England,Sardinia,Malta,Corsica', 'YTULWC8', 'Corsica', 'England,Sardinia,Malta', 'What island was captured in this awesome image?'),
  (41, 'Bear,Hamster,Cat,Wombat', '8vogQaN', 'Wombat', 'Bear,Hamster,Cat', 'What animal was captured in this awesome image?'),
  (42, 'France,Ghana,America,Switzerland', 'AhRYzB6', 'Switzerland', 'France,Ghana,America', 'In which country was this awesome image captured?'),
  (43, 'Bridge,Highway,Hogwarts Express,Viaduct', '3WUKuYd', 'Viaduct', 'Bridge,Highway,Hogwarts Express', 'What was captured in this awesome image?'),
  (44, 'Rome,Bern,Geneva,Lucerne', '3D6jIQD', 'Lucerne', 'Rome,Bern,Geneva', 'Where was this awesome image captured?'),
  (45, 'To Be Free,Cook,Dance,To Break Free', 'N7B9qts', 'To Break Free', 'To Be Free,Cook,Dance', 'I want to...'),
  (46, 'Horli-Hitta,Cafe Alpenblick,Hannigalpe,Aescher-Wildkirchli', 'Se66I3v', 'Aescher-Wildkirchli', 'Horli-Hitta,Cafe Alpenblick,Hannigalpe', 'What restaurant was captured in this awesome image?'),
  (47, 'St. Gall,Uri,Schwyz,Appenzell', 'MVhWfVv', 'Appenzell', 'St. Gall,Uri,Schwyz', 'From which canton from switzerland are this traditional clothes?'),
  (48, 'Portofino,Sirmione,Riva del Garda,Cinque Terre', 'R5tfvOP', 'Cinque Terre', 'Portofino,Sirmione,Riva del Garda', 'Which village was captured in this awesome image?'),
  (49, 'Amsterdam,Budapest,Varanasi,Venice', 'E2YMb20', 'Venice', 'Amsterdam,Budapest,Varanasi', 'Which city was captured in this image?'),
  (50, 'Explosion,Mushroom,Mount Kilimanjaro,Mount Etna', 'Gh2M23T', 'Mount Etna', 'Explosion,Mushroom,Kilimanjaro', 'What was captured in this image?'),
  (51, 'Mount Everest,Glacier,Stone Mine,Mount Kilimanjaro', 'zTOff', 'Mount Kilimanjaro', 'Mount Everest,Glacier,Stone Mine', 'What was captured in this awesome image?'),
  (52, 'Booderee,Kruger Park,Etosha Park,Yellow Stone', 'd60Kf1Z', 'Yellow Stone', 'Booderee,Kruger Park,Etosha Park', 'What park was captured in this awesome image?'),
  (53, 'AMG,RS,Alpina,M-Power', '44fNAtL', 'M-Power', 'AMG,RS,Alpina', 'What powers the car in this image?'),
  (54, 'Berlin,South Africa,Namibia,Zurich', 'YNNdMWl', 'Zurich', 'Berlin,South Africa,Namibia', 'Where was this image captured?'),
  (55, 'Louis Vuitton,Hermes,Saint Laurent,Gucci', 'AISgfr7', 'Gucci', 'Louis Vuitton,Hermes,Saint Laurent', 'What type of brand was captured in this awesome image?'),
  (56, 'Tuba,Tenor Horn,Trumpet,French Horn', 'zg5glJP', 'French Horn', 'Tuba,Tenor Horn,Trumpet', 'What instrument was captured in this image?'),
  (57, 'Black Widow,Red Racer,Ferrari,Red Pig', 'v81Ei16', 'Red Pig', 'Black Widow,Red Racer,Ferrari', 'How was it called?'),
  (58, 'Antoni Gaudi,Frank Gehry,Frank Lloyd Wright,Le Corbusier', 'm80ecgE', 'Le Corbusier', 'Antoni Gaudi,Frank Gehry,Frank Lloyd Wright', 'What architect build this house?'),
  (59, 'Candle,Mount K2,Polar Star,Mount Fuji', 'ooMJAQn', 'Mount Fuji', 'Candle,Mount K2,Polar Star', 'What was captured in this image?'),
  (60, 'Squirrel,Cotton,Flower,Bird', 'tXfvx', 'Squirrel', 'Cotton,Flower,Bird', 'What object was captured in this awesome image?');