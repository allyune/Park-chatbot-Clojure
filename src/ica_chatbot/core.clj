(ns ica-chatbot.core
  (:gen-class))

(defn -main
      [& args]
      (println "CHATBOT: Hello, I'm a chatbot. What is your name?>")
      (def   x (read-line))
      (println (str "CHATBOT: Nice to meet you,"\space\  x "."))
      )
