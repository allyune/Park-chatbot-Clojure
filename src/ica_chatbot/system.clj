(ns ica-chatbot.system
  (:require [clojure.string :as str]))

(defn bot-exit []
  "Terminates the session."
  (println "Bye for now!")
  (System/exit 0))

(defn print-out [output]
  "Prints the output while adding 'Park Bot' at the beginning."
  (println "Park Bot: " output)
  (flush))

(defn get-user-input [username]
  "Takes user input while adding username at the beginning of the prompt"
  (print (format "%s: " username))
  (flush)
  (str/lower-case (read-line)))
