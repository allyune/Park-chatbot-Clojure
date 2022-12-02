# Prague Park Chat Bot

Prague Park Chat Bot is an ELIZA-style chatbot that provides users with the information about parks in Prague. The chatbot is implemented in Clojure programming language and runs in Leiningen environment. The program sources information about parks from JSON file, as well as Google Places API.
## Requirements:
1. [Leiningen](https://leiningen.org/)
2. [Clojure](https://leiningen.org/)

## Installation
To run the chatbot, GitHub repository must be cloned using the following command:
```bash
git clone https://github.com/allyune/ica-chatbot.git
```

## Usage
Running the chatbot using Leiningen:
```bash
cd ica-chatbot
lein run -m ica-chatbot.core
```
Install dependencies:
```bash
lein deps
lein install
```

## Features

### Intent recognition
```
get-intent [input]
```
User input is matched against regular expressions. The first successful match returns user intent in a form of keyword. Example output:
```
>> (get intent "Is there a parking in Letná?")
>> :parking
```
### Reading park info from the JSON file
```
get-park-info [park intent]
```
Fetches information on specific intent of the specific park from JSON file converted to Clojure dictionary. Example output
```
>> (get-park-info :letna :parking)
>> true
```
## License

Copyright © 2022 Martina V., Jasmine K., Polina K.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
