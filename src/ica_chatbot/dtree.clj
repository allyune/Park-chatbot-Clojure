(ns ica-chatbot.dtree)

(def header ["name" "color" "size" "coat"])

(def records
  '(["German Shepherd" "tan with black saddle" "large" "double coat"]
    ["Bulldog" "brindle" "small " "short"]
    ["Labrador" "chocolate " "large" "short"]
    ["Golden Retriever" "gold" "large" "wavy"]
    ["French Bulldog" "brown" "small" "short"]
    ["Siberian Husky" "white " "large" "double coat"]
    ["Poodle" "white" "medium" "curly"]
    ["Chihuahua" "brown" "small" "short-haired"]
    ["Border Collie" "bicoloured" "medium" "double coat"]
    ["Dachshund" "chocolate" "short" "short"]
    ))

(defn record->dict [header record]
  (loop [header header
         record record
         result {}]
    (if (empty? header)
      result
      (recur (rest header)
             (rest record)
             (assoc result (keyword (first header)) (first record))))))

(defn records->dicts [header records]
  (map (fn [rec] (record->dict header rec)) records))

(defn feature-values [dicts feature]
  (distinct
   (map feature dicts)))

(def dicts (records->dicts header records))

(defn feature+value->predicate [feature value]
  [(keyword (str (name feature) "=" (clojure.string/replace value " " "_")))
   (fn [dict]
     (= (feature dict) value))])

(defn expand-dict+predicates [dict predicates]
  (loop [predicates predicates
         dict dict]
    (if (empty? predicates)
      dict
      (let [pred (first predicates)]
        (recur (rest predicates)
               (assoc dict
                      (first pred)
                      ((second pred) dict)))))))

(def predicates
  (apply concat
         (for [feature '(:color :size :coat)]
           (for [value (feature-values dicts feature)]
             (feature+value->predicate feature value)))))

(def dicts+
  (map (fn [dict] (expand-dict+predicates dict predicates)) dicts))

(def predicate-kws (map first predicates))

(defn split-dicts [dicts feature]
  [(filter (fn [dict] (feature dict)) dicts),
   (filter (fn [dict] (not (feature dict))) dicts)])

(defn get-predicates-scores [dicts predicates-kws]
  (sort
   (fn [a b] (< (second a) (second b)))
   (filter (fn [x] (and (> (nth x 2) 0)
                        (> (nth x 3) 0)))
           (for [pred predicates-kws]
             (let [split (split-dicts dicts pred)
                   one (first split)
                   two (second split)
                   onec (count one)
                   twoc (count two)
                   ratio (if (> (min onec twoc) 0)
                           (/ (max onec twoc)
                              (min onec twoc))
                           0)]
               [pred ratio onec twoc])))))

(defn build-dtree [dicts answer predicates-kws]
  (if (= (count dicts) 1)
    (let [dict (first dicts)]
      {:resolution (str "It is " (:name dict) "."),
       :answer answer})
    (let [scores (get-predicates-scores dicts predicates-kws)
          spred (first (first scores))
          split (split-dicts dicts spred)
          one (first split)
          two (second split)]
      {:answer answer,
       :question (str
                  "Is the "
                   (clojure.string/join " " (clojure.string/split (clojure.string/join " " (clojure.string/split (name spred) #"=")) #"_"))
                  "?")
       :children [
                  (build-dtree one "yes" (map first (rest scores)))
                  (build-dtree two "no" (map first (rest scores)))
                  ]
       }
    )))

(def the-dtree
  (build-dtree dicts+ nil predicate-kws))

(defn dtree-find-child [node answer]
  (first
   (filter
    (fn [child]
      (= (:answer child) answer))
    (:children node))))
