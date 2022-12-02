(ns ica-chatbot.core
  (:use [ica-chatbot.dictionary])
  (:require [ica-chatbot.regex :as regex :only [get-intent]])
  (:require [ica-chatbot.answers :as answers :only [print-transportation print-park-info]])
  (:require [ica-chatbot.reviews :as reviews :only [print-latest-reviews]])
  (:require [ica-chatbot.system :as system :only [print-out get-user-input bot-exit]])
  (:require [clojure.string :as str]))

(defn start-bot [username]
  "A starting function"
  "This functions use conditional which is represented by the keyword case.
    This format begins with asking the user a question. The response is evaluated.
    The response is read in and stored in input and then compared to the positive set. If the response is positive,
    then this function stops and the next is called."
  (loop [park :letna status :start]
    (system/print-out (get prompts status))
    (let [input (system/get-user-input username)
          intent (regex/get-intent input)]
      (case intent
        :exit (system/bot-exit)
        :reviews (reviews/print-latest-reviews park)
        :unknown (system/print-out "Sorry, I don't understand or I have no such info. Try to rephrase.")
        :transportation (answers/print-transportation park)
        (answers/print-park-info park intent))
      (recur :letna :in-progress))))

(defn -main
  "Main function"
  [& args]
  (system/print-out "Hello, I'm a park chatbot. How can I call you?")
  (def name (read-line))
  (system/print-out (str "Nice to meet you, " name "."))
  (system/print-out "I can give you information regarding Letna park. Please type YES to get more info or type :done to exit anytime")
  (let [input (system/get-user-input name)]
    (if-not (contains? Positive input) (system/bot-exit)
            (start-bot name))))
