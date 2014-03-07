(ns gz-reduction.sql
  (:require [paneer.core :as p]
            [paneer.db :refer :all]
            [korma.core :refer :all]
            [korma.db :refer :all]
            [clojure.string :refer [split replace]]
            [gz-reduction.core :refer [format-tree-text]]))

(defn column-names
  [prefix name-prefix names]
  (loop [ns names index 0 accum []]
    (if-let [n (first ns)]
      (recur (rest ns) (+ index 1) (cons (str prefix name-prefix "-" index "_" (format-tree-text n)) accum))
      accum)))

(defn answer-columns
  [prefix {:keys [answers checkboxes]}]
  (let [checkboxes (column-names prefix "x" checkboxes)
        answers (column-names prefix "a" answers)]
    (concat checkboxes answers)))

(defn tree-to-columns
  [{:keys [prefix questions]}]
  (loop [qs questions index 0 accum []]
    (if-let [q (first qs)] 
      (let [question-prefix (str prefix "-" index "_" (:group (first qs)) "_")] 
        (recur (rest qs) (+ index 1) (cons (answer-columns question-prefix q) accum)))
      accum)))

(defn- uri-to-db-map
  [uri]
  (let [uri (java.net.URI. uri)
        [username password] (split (.getUserInfo uri) #":")]
    {:db (apply str (drop 1 (.getPath uri)))
     :user username
     :password password
     :host (.getHost uri)
     :port (.getPort uri)}))
 
(defn create-table-from-tree
  [tree db]
  (let [columns (flatten (tree-to-columns tree))
        base-tbl  (-> (p/create*)
                      (p/table (:prefix tree))
                      (p/varchar :subject_id 24))]
    (execute (reduce #(p/float %1 (keyword %2)) base-tbl columns) :db db)))

(defn flatten-answer
  [q m [a v]]
  (assoc m (keyword (str (replace q #":" "") "_" a)) v))

(defn flatten-answers
  [m [q as]]
  (reduce (partial flatten-answer q) m as))

(defn to-db-map
  [[sid c]]
  (reduce flatten-answers {:subject_id sid} c))

(defn to-sql
  [tree db cs]
  (let [db (create-db (postgres (uri-to-db-map db)))] 
    (default-connection db)
    (p/if-not-exists
      (p/drop-table (:prefix tree)))
    (create-table-from-tree tree db)
    (doseq [batch (partition 100 (map to-db-map cs))]
      (insert (:prefix tree)
              (values batch)))))
