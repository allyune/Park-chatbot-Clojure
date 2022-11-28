(ns ica-chatbot.core
  (:gen-class))



(use 'clojure.java.browse)

;;Positive responses
(def Positive #{"Yes" "YES" "OK" "yes" "Yes please" "yes please"})


;;Negative responses
(def Negative #{"No" "Nope" "NO" "no" "NOPE"})

;;Other responses
(def Other #{"Not sure" "Maybe" "not sure"})




(defn park-info []

(loop [state :start]
(println "ChatBot: What kind of information would you like to get?")
(let [input (str/lower-case (read-line)) intent (get-intent input)]
(cond

(= intent "transport")
(do
(get-transportation))


(= intent "Park info")
(do
(get-park-info))


else (do (println "")
(recur state))))))


(defn park-info-reviews []
      (println "ChatBot: First i would like to ask you if you would like to see some reviews for this park before we start? Please type YES to get reviews or type NO to continue")
      (loop [state :start]
            (let [input (read-line)]
                  (cond
                        (contains? Positive input) (fetch-latest-reviews )
                        (contains? Negative input) (park-info)
                        (contains? Other input) (System/exit 0)
                        ))))






(defn start-bot []
  "A starting function"
  "This functions use conditional which is represented by the keyword cond.
  This format begins with asking the user a question. The response is evaluated.
  The response is read in and stored in input and then compared to the positive set. If the response is positive,
  then this function stops and the next is called."

  (println "ChatBot: I can give you information regarding Letna park. Please type YES to get more info or type  :done to exit anytime")
  (loop [state :start]
    (let [input (read-line)]
          (if-not (= input ":done")
      (cond
        (contains? Positive input) (park-info-reviews)
        (contains? Negative input) "You can exit anytime typing :done"
        (contains? Other input) (System/exit 0)
        :else (start-bot)))))




(defn -main
  "Main function"
  [& args]
  (println "ChatBot: Hello, I'm a park chatbot. What is your name?")
  (def input (read-line))
  (println (str "ChatBot: Nice to meet you," \space \  input "."))
  (start-bot))