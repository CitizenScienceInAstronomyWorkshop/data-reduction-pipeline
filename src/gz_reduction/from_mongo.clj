(ns gz-reduction.from-mongo
  (:require [monger.core :refer [connect! get-db set-db!]]
            [monger.collection :as mc]
            [monger.conversion :refer [from-db-object to-object-id]]
            [gz-reduction.core :refer [run-reduction]]
            [gz-reduction.trees :refer [get-tree]]
            [gz-reduction.sql :refer [to-sql]]))

(defn init-connection!
  [db]
  (connect!)
  (set-db! (get-db db)))

(defn lazy-collection
  [db col-name workflow]
  (if-not (bound? (find-var 'monger.core/*mongodb-connection*))
    (init-connection! db))
  (let [cursor (mc/find col-name {:workflow_id workflow} [:subject_ids :user_id :user_ip :annotations])
        seq-fn (fn seq-fn [cursor]
                 (lazy-seq (cons (from-db-object (.next cursor) true) 
                                 (if (.hasNext cursor)
                                   (seq-fn cursor)
                                   nil))))]
    (seq-fn cursor)))

(defn workflow-to-id
  [wfl]
  (let [wfls {"candels" "50251c3b516bcb6ecb000001",
              "sloan" "50251c3b516bcb6ecb000002",
              "ukidss" "52449f803ae740540e000001",
              "ferengi" "5249cbc33ae74070ed000001"}]
    (to-object-id (wfls wfl))))

(defn -main
  [& [db col workflow sql-db]]
  (let [tree (get-tree workflow)] 
    (to-sql tree sql-db (run-reduction (lazy-collection db col (workflow-to-id workflow)) 
                                       tree
                                       (fn [uid] 1) 
                                       identity))))
