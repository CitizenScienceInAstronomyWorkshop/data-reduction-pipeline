(ns gz-reduction.core
  (:require [clojure.algo.generic.functor :refer [fmap]]
            [clojure.string :refer [split replace lower-case]]))

(defn subject-id
  [c]
  (update-in c [:subject_ids] (comp str first)))

(defn answer?
  [an]
  (not (or (contains? an :user_agent) (contains? an :lang))))

(defn remove-non-answers
  [c]
  (update-in c [:annotations] #(filter answer? %)))

(defn weight-answer
  [w ans]
  [(first (keys ans)) (first (vals ans)) w])

(defn weight-answers
  [w-fn c]
  (let [uid (or (:user_id c) (:user_ip c))] 
    (update-in c [:annotations] #(map (partial weight-answer (w-fn uid)) %))))

(defn reduce-answer
  [m [_ a w]] 
  (cond
    (vector? a) (reduce reduce-answer m (map #(vector nil % w) a))
    true (if (m a) 
           (update-in m [a] + w)
           (assoc m a w))))

(defn vote-fractions
  [ans]
  (let [count (reduce + (vals ans))]
    (fmap (comp float #(/ % count)) ans)))

(defn reduce-answers
  [ans]
  (reduce reduce-answer {} ans))

(defn format-tree-text
  [s]
  (replace (lower-case s) #"\s" "_" ))

(defn annotate-answers
  [task m [a v]]
  (println (name a) (.getChoiceLabel task (name a)) )
  (if-let [answer (.getChoiceLabel task (name a))]
    (do (println answer) (assoc m (str (format-tree-text answer)) v))
    m))

(defn annotate-question
  [qs m [q as]]
  (let [question (get qs (name q))]
    (assoc m 
           (str (name q) (when (.readableKey question) (str "_" (.readableKey question))))
           (reduce (partial annotate-answers question) {} as))))

(defn annotate-tree
  [tree ans]
  (reduce (partial annotate-question (.getTasks tree)) {} ans))

(defn group-answers
  [selector tree [sid cs]]
  {sid (->> (map :annotations cs)
            (reduce concat)
            (group-by first)
            (fmap (comp selector vote-fractions reduce-answers)))})

(defn run-reduction
  [col tree weight-fn select-fn]
  (->> (map (comp (partial weight-answers weight-fn) remove-non-answers subject-id) col)
       (group-by :subject_ids)
       (filter #(= (count (second %)) 40))
       (map (partial group-answers select-fn tree))
       (reduce merge)
       (fmap (partial annotate-tree tree))))
