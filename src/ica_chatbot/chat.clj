(ns ica-chatbot.chat
  (:use [ica-chatbot.dictionary]
        [ica-chatbot.state]
        [org.clojars.cognesence.matcher.core])
  (:require [ica-chatbot.answers :as answers :only [get-available-info print-transportation print-park-info print-recommendations]]
            [ica-chatbot.system :as system :only [print-out get-user-input]]
            [clojure.string :as str]))

(defn recommendation? [intent input]
  (and (not (nil? intent))
                 (boolean (re-find #"(where|recommend)" input))))

(defn park-request? [park intent]
  (not (every? nil? (list park intent))))

(defn empty-request? [park intent]
  (every? nil? (list park intent)))

(defn park-answer [park intent]
  (mcond [(list park intent)]
    ((?p nil) (do (system/print-out (format "What would you like to know about %s?" (get park-names (? p))))
                  (system/print-out (format "I have information about %s" (answers/get-available-info (? p)))) "--"))
    ((nil ?i) (do (system/print-out "What park would you like to get this info for?") "--"))
    ((?p ?i) (do (answers/get-answer (? p) (? i)) "--"))))

(defn start-bot [username]
  "A starting function"
  "This functions use conditional which is represented by the keyword case.
    This format begins with asking the user a question. The response is evaluated.
    The response is read in and stored in input and then compared to the positive set. If the response is positive,
    then this function stops and the next is called."
  (system/print-out "What would you like to know?")
  (loop [old-state state]
    (let [input (system/get-user-input username)
          new-state (update-state input old-state)
          new-park (mfind* ['((park ?p)) new-state] (? p))
          new-intent (mfind* ['((intent ?i)) new-state] (? i))]
          (if (empty-request? new-park new-intent)
            (do
              (system/unknown-input-reaction)
              (recur old-state))
            (do
              (cond
                (recommendation? new-intent input) (answers/print-recommendations new-intent)
                ; (dog-request? input) (dtree/identify-dog)
                (park-request? new-park new-intent) (park-answer new-park new-intent))
              (recur new-state))))))
