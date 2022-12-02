(ns ica-chatbot.system
  (:require [clojure.string :as str]))

(defn bot-exit []
  (println "Bye for now!")
  (System/exit 0))

(defn print-out [output]
  (println "Park Bot: " output)
  (flush))

(defn get-user-input [username]
  (print (format "%s: " username))
  (flush)
  (str/lower-case (read-line)))
