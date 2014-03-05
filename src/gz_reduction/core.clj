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

(defn reduce-answers
  [ans]
  (reduce reduce-answer {} ans))

(defn to-index
  [s & [offset]]
  (let [offset (or offset 0)] 
    (cond
      (vector? s) (map to-index s offset)
      true (- (Integer/parseInt (second (split (name s) #"-"))) offset))))

(defn format-tree-text
  [s]
  (replace (lower-case (:text s)) #"\s" "_" ))
 
(defn select-fn
  [ans]
  (first (reduce #(max-key second %1 %2) ans)))

(defn next-question
  [questions ]
  (let [q-texts (map :text questions)] 
    (fn [q a] 
      (let [next (if-let [n (:leadsTo a)]
                   n
                   (:leadsTo q))]
        (if (:end next)
          next
          (.indexOf q-texts next))))))

(comment (defn resolve-answer
           []
           (cond 
             (seq? (ans q-index)) ()
             true (nth (:answers question) (ans q-index)))))

(defn prune-tree
  [{:keys [questions]} ans]
  (let [next-fn (next-question questions)] 
    (loop [q-index 0 acc {}]
      (let [question (nth questions q-index)
            answer ans
            next (next-fn question answer)]
        (if (= :end next)
          (assoc acc (:text question) (:text answer))
          (recur next (assoc acc (:text question) (:text answer))))))))

(defn at-string-index
  [xs index-str & [offset]]
  ;; Assumes any out of bounds are answers are caused by recording errors and silently ignores
  (try (nth xs (to-index index-str offset))
       (catch Exception e nil)))

(defn annotate-answers
  [{:keys [answers checkboxes]} m [a v]]
  (if-let [answer (if (= \a (first a)) 
                      (at-string-index answers a)
                      (at-string-index checkboxes a))] 
    (assoc m (str a "_" (format-tree-text answer)) v)
    m))

(defn annotate-question
  [qs m [q as]]
  (let [question (at-string-index qs q)] 
    (assoc m 
           (str q "_" (:group question))
           (reduce (partial annotate-answers question) {} as))))

(defn annotate-tree
  [{:keys [questions]} ans]
  (reduce (partial annotate-question questions) {} ans))

(defn group-answers
  [selector tree [sid cs]]
  {sid (->> (map :annotations cs)
            (reduce concat)
            (group-by first)
            (fmap (comp selector reduce-answers)))})

(defn run-reduction
  [col tree weight-fn select-fn]
  (->> (map (comp (partial weight-answers weight-fn) remove-non-answers subject-id) col)
       (group-by :subject_ids)
       (filter #(= (count (second %)) 40))
       (map (partial group-answers select-fn tree))
       (reduce merge)
       (fmap (partial annotate-tree tree))))
