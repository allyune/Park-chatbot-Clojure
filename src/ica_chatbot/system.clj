(ns ica-chatbot.system)

(defn bot-exit []
  (println "Bye for now!")
  (System/exit 0))

(defn print-out [output]
  (println "Park Bot: " output)
  (flush))

(defn get-user-input [username]
  (print (format "%s: " username))
  (flush)
  (read-line))
