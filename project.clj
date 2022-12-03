(defproject ica-chatbot "SC ICA 1"
  :description "Prague Park Chat Bot is an ELIZA-style chatbot that provides users with the information about parks in Prague. The chatbot is implemented in Clojure programming language and runs in Leiningen environment. The program sources information about parks from JSON file, as well as Google Places API."
  :url "none"
  :license {:name "Apache License, Version 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/data.json "2.4.0"]
                 [org.clojars.cognesence/matcher "1.0.1"]]
  :main ^:skip-aot ica-chatbot.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}}
  :plugins [[lein-cljfmt "0.9.0"]
            [lein-codox "0.10.8"]]

  :codox {:namespaces [ica-chatbot.core ica-chatbot.regex ica-chatbot.answers ica-chatbot.system ica-chatbot.dictionary ica-chatbot.reviews]
          :output-path "docs"})
