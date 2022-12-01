(ns ica-chatbot.core
  (:require [ica-chatbot.parser :as parser])
  (:require [ica-chatbot.answers :as answers])
  (:require [ica-chatbot.reviews :as reviews])
  (:require [clojure.string :as str])
  (:use [ica-chatbot.dictionary]))


(defn bot-exit []
  (println "Bye for now!")
  (System/exit 0))

(defn print-bot [output]
  (println "Park Bot: " output)
  (flush))

(defn get-user-input [username]
  (print (format "%s: " username))
  (flush)
  (read-line))

(defn start-bot [username]
  "A starting function"
  "This functions use conditional which is represented by the keyword case.
  This format begins with asking the user a question. The response is evaluated.
  The response is read in and stored in input and then compared to the positive set. If the response is positive,
  then this function stops and the next is called."
  (loop [park :letna status :start]
         (print-bot (get prompts status))
         (let [input (get-user-input username) intent (parser/get-intent input)]
           (case intent
            :exit (bot-exit)
            :transportation (answers/print-transportation park)
            :reviews (reviews/print-latest-reviews park)
            :unknown (println "Sorry, I don't have this information. Try to rephrase.")
            (answers/print-park-info park intent))
         (recur :letna :in-progress))))


(defn -main
  "Main function"
  [& args]
  (print-bot "Hello, I'm a park chatbot. How can I call you?")
  (def name (read-line))
  (print-bot (str "Nice to meet you, "name "."))
  (print-bot "I can give you information regarding Letna park. Please type YES to get more info or type :done to exit anytime")
  (let [input (get-user-input name)]
        (if-not (contains? Positive input) (bot-exit)
          (start-bot name))))
