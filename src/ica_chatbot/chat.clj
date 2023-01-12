(ns ica-chatbot.chat
  (:use [ica-chatbot.dictionary]
        [ica-chatbot.state]
        [org.clojars.cognesence.matcher.core])
  (:require [ica-chatbot.answers :as answers
              :only [get-available-info get-available-info-all-parks print-transportation print-park-info print-recommendations]]
            [ica-chatbot.system :as system :only [print-out get-user-input]]
            [ica-chatbot.regex :as regex :only [get-module]]
            [clojure.string :as str]))

(defn recommendation? [intent input]
  (and (not (nil? intent))
                 (boolean (re-find #"(where|recommend)" input))))

(defn park-request? [park intent]
  (not (every? nil? (list park intent))))

(defn empty-request? [park intent module]
  (every? nil? (list park intent module)))

(defn park-respond [park intent]
  (mcond [(list park intent)]
    ((?p nil) (do (system/print-out (format "What would you like to know about %s?" (get park-names (? p))))
                  (system/print-out (format "I have information about %s" (answers/get-available-info (? p)))) "--"))
    ((nil ?i) (do (system/print-out "What park would you like to get this info for?") "--"))
    ((?p ?i) (do (answers/get-answer (? p) (? i)) "--"))))

(defn recommendation-respond [input park]
  (let [intent (regex/get-intent input)]
  (cond
    (not (nil? park)) (system/print-out "Sorry I can't give recommendations for specific park. Try typing something like 'Recommend park for skating' ")
    (nil? intent) (system/print-out (format "I can recommend parks for %s" (answers/get-available-info-all-parks)))
    :else (answers/print-recommendations intent))))

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
          new-intent (mfind* ['((intent ?i)) new-state] (? i))
          new-module (mfind* ['((module ?m)) new-state] (? m))]
          (if (empty-request? new-park new-intent new-module)
            (do
              (system/unknown-input-reaction)
              (recur old-state))
            (do
              (cond
                (= new-module :recommend) (recommendation-respond input new-park)
                (= new-module :dtree) (evaluate-dtree)
                (park-request? new-park new-intent) (park-respond new-park new-intent))
              (recur new-state))))))
