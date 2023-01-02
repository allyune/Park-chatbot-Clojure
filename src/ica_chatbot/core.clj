(ns ica-chatbot.core
  (:use [ica-chatbot.dictionary])
  (:use [org.clojars.cognesence.matcher.core])
  (:require [ica-chatbot.regex :as regex :only [get-intent]])
  (:require [ica-chatbot.answers :as answers :only [print-transportation print-park-info]])
  (:require [ica-chatbot.reviews :as reviews :only [print-latest-reviews]])
  (:require [ica-chatbot.system :as system :only [print-out unknown-input-reaction get-user-input bot-exit]])
  (:require [clojure.string :as str]))

(defn start-bot [username]
  "A starting function"
  "This functions use conditional which is represented by the keyword case.
    This format begins with asking the user a question. The response is evaluated.
    The response is read in and stored in input and then compared to the positive set. If the response is positive,
    then this function stops and the next is called."
  (loop [park nil status :start]
    (system/print-out (get prompts status))
    (let [input (system/get-user-input username)
          park-intent (list (regex/get-park input) (regex/get-intent input))]
          (mcond [park-intent]
          ((nil :exit) (system/bot-exit))
          ((nil nil) (do (system/unknown-input-reaction) "----" ))
          ((?p nil) (do (system/print-out (format "Park is %s, intent is nil" (first park-intent)))) "----")
          ((nil ?i) (do (system/print-out (format "Park is nil, intent is %s" (second park-intent)))) "----")
          ((?p ?i) (system/print-out (format "Park is %s, intent is %s" (first park-intent) (second park-intent)))))
          (recur (first park-intent) :in-progress))))



      ; (case intent
      ;   :exit (system/bot-exit)
      ;   :reviews (reviews/print-latest-reviews park)
      ;   :unknown (system/unknown-input-reaction)
      ;   :transportation (answers/print-transportation park)
      ;   (answers/print-park-info park intent))
      ; (recur :letna :in-progress))))

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
