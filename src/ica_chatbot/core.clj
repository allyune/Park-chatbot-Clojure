(ns ica-chatbot.core
  (:use [ica-chatbot.dictionary])
  (:use [ica-chatbot.state])
  (:use [org.clojars.cognesence.matcher.core])
  (:use [clojure.set])
  (:require [ica-chatbot.regex :as regex :only [get-intent]])
  (:require [ica-chatbot.answers :as answers :only [print-transportation print-park-info]])
  (:require [ica-chatbot.reviews :as reviews :only [print-latest-reviews]])
  (:require [ica-chatbot.system :as system :only [print-out unknown-input-reaction get-user-input bot-exit]])
  (:require [clojure.string :as str]))


(defn get-answer [park intent]
    (case intent
      :exit (system/bot-exit)
      :reviews (reviews/print-latest-reviews park)
      :unknown (system/unknown-input-reaction)
      :transportation (answers/print-transportation park)
      (answers/print-park-info park intent)))

(defn valid-request? [park intent]
  (not (every? nil? (list park intent))))

(defn start-bot [curr-state username]
  "A starting function"
  "This functions use conditional which is represented by the keyword case.
    This format begins with asking the user a question. The response is evaluated.
    The response is read in and stored in input and then compared to the positive set. If the response is positive,
    then this function stops and the next is called."
  (system/print-out "What would you like to know?")
  (loop [old-state curr-state]
    (let [input (system/get-user-input username)
          new-state (analyze-input input old-state)
          new-park (mfind* ['((park ?p)) new-state] (? p))
          new-intent (mfind* ['((intent ?i)) new-state] (? i))]
          (if (valid-request? new-park new-intent)
            (do
              (mcond [(list new-park new-intent)]
              ;TODO: Implement a list of available information
                ((?p nil) (do (system/print-out (format "What would you like to know about %s?" (get park-names (? p)))) "--"))
                ((nil ?i) (do (system/print-out "What park would you like to get this info for?") "--"))
                ((?p ?i) (do (get-answer (? p) (? i)) "--")))
              (recur new-state))
            (do
              (system/unknown-input-reaction)
              (recur old-state))))))


(defn -main
  "Main function containing starting sequence of the bot."
  [& args]
  (system/print-out "Hello, I'm a park chatbot. How can I call you?")
  (let [curr-state (apply-op state (:add-username ops) (read-line))
        username (mfind* ['((username ?u)) curr-state] (? u))]
  (system/print-out (str "Nice to meet you, " username "."))
  (start-bot curr-state username)))
