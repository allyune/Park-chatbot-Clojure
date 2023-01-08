(ns ica-chatbot.core
  (:require [ica-chatbot.system :as system :only [print-out get-user-input bot-exit]]
            [clojure.string :as str]
            [ica-chatbot.chat :as chat :only [start-bot]]))

(defn -main
  "Main function containing starting sequence of the bot."
  [& args]
  (system/print-out "Hello, I'm a park chatbot. How can I call you?")
  (let [username (read-line)]
    (system/print-out (str "Nice to meet you, " username "."))
    (chat/start-bot username)))
