(ns ica-chatbot.core
  (:use [ica-chatbot.dictionary])
  (:use [org.clojars.cognesence.matcher.core])
  (:use [clojure.set])
  (:require [ica-chatbot.regex :as regex :only [get-intent]])
  (:require [ica-chatbot.answers :as answers :only [print-transportation print-park-info]])
  (:require [ica-chatbot.reviews :as reviews :only [print-latest-reviews]])
  (:require [ica-chatbot.system :as system :only [print-out unknown-input-reaction get-user-input bot-exit]])
  (:require [clojure.string :as str]))

(def state '#{(park nil) (intent nil) (username nil)})

(defn get-answer [park intent]
    (case intent
      :exit (system/bot-exit)
      :reviews (reviews/print-latest-reviews park)
      :unknown (system/unknown-input-reaction)
      :transportation (answers/print-transportation park)
      (answers/print-park-info park intent)))

(def ops
    '{:add-park {:pre ((park nil) (intent ?i) (username ?u))
                 :del ((park nil))
                 :add park}
      :add-intent {:pre ((park ?p) (intent nil) (username ?u))
                   :del ((intent nil))
                   :add intent}
      :add-username {:pre ((park ?p) (intent ?i) (username nil))
                    :del ((username nil))
                    :add username}
      :update-park {:pre ((park ?p) (intent ?i) (username ?u))
                    :del ((park ?p))
                    :add park}
      :update-intent {:pre ((park ?p) (intent ?i) (username ?u))
                   :del ((intent ?i))
                   :add intent}})


(defn apply-op
  [state
  {:keys [pre del add]}
   new-val]
(mfind* [pre state]
(union #{(list add new-val)}
(difference
state
(mout del)))))

(defn update-both [old-state park intent]
  (apply-op (apply-op old-state (:update-park ops) park) (:update-intent ops) intent))

(defn add-both [old-state park intent]
  (apply-op (apply-op old-state (:add-park ops) park) (:add-intent ops) intent))

(defn add-park [old-state park]
  (apply-op old-state (:add-park ops) park))

(defn update-park [old-state park]
  (apply-op old-state (:update-park ops) park))

(defn add-intent [old-state intent]
  (apply-op old-state (:add-intent ops) intent))

(defn update-intent [old-state intent]
  (apply-op old-state (:update-intent ops) intent))

(defn add-park-update-intent [old-state park intent]
  (apply-op (apply-op old-state (:add-park ops) park) (:update-intent ops) intent))

(defn update-park-add-intent [old-state park intent]
  (apply-op (apply-op old-state (:update-park ops) park) (:add-intent ops) intent))

(defn analyze-input [input old-state]
  (if (= (regex/get-intent input) :exit)
    (system/bot-exit))
    (let [curr-park (mfind* ['((park ?p)) old-state] (? p))
          curr-intent (mfind* ['((intent ?i)) old-state] (? i))
          new-park (regex/get-park input)
          new-intent (regex/get-intent input)]
          (cond
            (and (nil? curr-park) (nil? curr-intent))
              (add-both old-state new-park new-intent)
            (and (not (nil? curr-park)) (nil? curr-intent))
              (mcond [(list new-park new-intent)]
                ((nil ?i) (add-intent old-state new-intent))
                ((?p nil) (update-park old-state (? p)))
                ((?p ?i) (update-park-add-intent old-state (? p) (? i))))
            (and (nil? curr-park) (not (nil? curr-intent)))
              (mcond [(list new-park new-intent)]
                ((?p nil) (add-park old-state (? p)))
                ((nil ?i) (update-intent old-state (? i)))
                ((?p ?i) (add-park-update-intent old-state (? p) (? i))))
            (not (some nil? (list curr-park curr-intent)))
              (mcond [(list new-park new-intent)]
                ((?p nil) (update-both old-state (? p) nil))
                ((nil ?i) (update-intent old-state (? i)))
                ((?p ?i) (update-both old-state (? p) (? i)))))))


(defn valid-request? [park intent]
  (not (every? nil? (list park intent))))


(defn start-bot [curr-state]
  "A starting function"
  "This functions use conditional which is represented by the keyword case.
    This format begins with asking the user a question. The response is evaluated.
    The response is read in and stored in input and then compared to the positive set. If the response is positive,
    then this function stops and the next is called."

  (system/print-out "What would you like to know?")
  (loop [old-state curr-state
         username (mfind* ['((username ?u)) old-state] (? u))]
    (let [input (system/get-user-input username)
          new-state (analyze-input input old-state)
          new-park (mfind* ['((park ?p)) new-state] (? p))
          new-intent (mfind* ['((intent ?i)) new-state] (? i))]
          (if (valid-request? new-park new-intent)
            (do
              (mcond [(list new-park new-intent)]
                ((?p nil) (do (system/print-out (format "What would you like to know about %s?" (get park-names (? p)))) "--"))
                ((nil ?i) (do (system/print-out "What park would you like to get this info for?") "--"))
                ((?p ?i) (do (get-answer (? p) (? i)) "--")))
              (recur new-state username))
            (do
              (system/unknown-input-reaction)
              (recur old-state username))))))


(defn -main
  "Main function containing starting sequence of the bot."
  [& args]
  (system/print-out "Hello, I'm a park chatbot. How can I call you?")
  (let [curr-state (apply-op state (:add-username ops) (read-line))
        username (mfind* ['((username ?u)) curr-state] (? u))]
  (system/print-out (str "Nice to meet you, " username "."))
  (start-bot curr-state)))
