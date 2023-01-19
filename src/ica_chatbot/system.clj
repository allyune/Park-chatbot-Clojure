(ns ica-chatbot.system
  (:require [clojure.string :as str]))

(defn bot-exit
  "Terminates the session."
  []
  (println "Bye for now!")
  (System/exit 0))

(defn print-out
  "Prints the output while adding 'Park Bot' at the beginning."
  [output]
  (println "Park Bot: " output)
  (flush))

(defn get-user-input
  "Takes user input while adding username at the beginning of the prompt."
  [username]
  (print (format "%s: " username))
  (flush)
  (str/lower-case (read-line)))

(defn unknown-input-reaction
  "Prints a reaction to the unidentified inout."
  []
  (print-out "Sorry, I don't understand or I have no such info. Try to rephrase."))
