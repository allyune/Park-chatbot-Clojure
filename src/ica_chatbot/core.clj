
    (ns ica-chatbot.core
    (:require [ica-chatbot.parser :as parser])
    (:require [ica-chatbot.answers :as answers])
    (:require [ica-chatbot.reviews :as reviews])
    (:require [clojure.string :as str]))


  ;;Positive responses
  (def Positive #{"Yes" "YES" "OK" "yes" "Yes please" "yes please"})

  ;;Negative responses
  (def Negative #{"No" "Nope" "NO" "no" "NOPE"})

  ;;Other responses
  (def Other #{"Not sure" "Maybe" "not sure"})

  (defn bot-exit []
    (println "Bye for now!")
    (System/exit 0))


  (defn start-bot []
    "A starting function"
    "This functions use conditional which is represented by the keyword case.
    This format begins with asking the user a question. The response is evaluated."
    (loop [park :letna]
      (println "What would you like to know about the park? Maybe some information about transportation,
       general park information or maybe to see reviews?")
      (let [input (str/lower-case (read-line)) intent (parser/get-intent input)]
        (case intent
          :exit (bot-exit)
          :transportation (answers/get-transportation park)
          :reviews (reviews/fetch-latest-reviews "ChIJjQpb2NqUC0cRaCCIBrCgONQ")
          :unknown (println "Sorry, I don't understand. Try to rephrase")
          :help  (println "I am sorry i could not help you, i am here just to give basic information about Letna park")
          (answers/print-park-info intent park))
        (recur :letna))))


  (defn -main
    "Main function"
    [& args]
    (println "ChatBot: Hello, I'm a park chatbot. What is your name?")
    (def name (read-line))
    (println (str "ChatBot: Nice to meet you, "name "."))
    (println "ChatBot: I can give you information regarding Letna park.
    Please type YES to get more info or type NO to exit")
    (let [input (read-line)]
      (if-not (contains? Positive input) (bot-exit)
                                         (start-bot))))