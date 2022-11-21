(ns ica-chatbot.core
  (:gen-class))

(use 'clojure.java.browse)

;;Positive responses
(def Positive #{"Yes" "YES" "OK" "yes" "Yes please" "yes please"})


;;Negative responses
(def Negative #{"No" "Nope" "NO" "no" "NOPE"})

;;Other responses
(def Other #{"Not sure" "Maybe" "not sure"})


(defn start
      []
      (println "Do you wish to get more information about Letná park in Prague?>")
      (let [x (read-line)]
            (cond
                  (contains? Positive x) ()
                  (contains? Negative x) "CHATBOT: I am here to help with information about parks in Prague!"
                  (contains? Other x) "CHATBOT: Sounds like an issue"
                  :else (start))))


(defn -main
      [& args]
      (println "CHATBOT: Hello, I'm a chatbot. What is your name?>")
      (def   x (read-line))
      (println (str "CHATBOT: Nice to meet you,"\space\  x "."))
      (start))
