(ns ica-chatbot.core
  (:use [ica-chatbot.dictionary])
  (:use [ica-chatbot.parser :only [get-intent]])
  (:use [ica-chatbot.answers :only [print-transportation print-park-info]])
  (:use [ica-chatbot.reviews :only [print-latest-reviews]])
  (:use [ica-chatbot.system :only [print-out get-user-input bot-exit]])
  (:require [clojure.string :as str]))

(defn start-bot [username]
  "A starting function"
  "This functions use conditional which is represented by the keyword case.
    This format begins with asking the user a question. The response is evaluated.
    The response is read in and stored in input and then compared to the positive set. If the response is positive,
    then this function stops and the next is called."
  (loop [park :letna status :start]
    (print-out (get prompts status))
    (let [input (get-user-input username)
          intent (get-intent input)]
      (case intent
        :exit (bot-exit)
        :reviews (print-latest-reviews park)
        :unknown (print-out "Sorry, I don't understand or I have no such info. Try to rephrase.")
        :transportation (print-transportation park)
        (print-park-info park intent))
      (recur :letna :in-progress))))

(defn -main
  "Main function"
  [& args]
  (print-out "Hello, I'm a park chatbot. How can I call you?")
  (def name (read-line))
  (print-out (str "Nice to meet you, " name "."))
  (print-out "I can give you information regarding Letna park. Please type YES to get more info or type :done to exit anytime")
  (let [input (get-user-input name)]
    (if-not (contains? Positive input) (bot-exit)
            (start-bot name))))
