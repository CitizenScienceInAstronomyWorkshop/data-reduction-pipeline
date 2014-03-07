(ns gz-reduction.trees
  (:require [jdecisiontree.core :refer :all]))

(defn- gz-choice-transformer
  [q-type m [sym label & opts]]
  (let [index (count m)
        prefix (if (= q-type :single) "a-" "x-")]
    (cons (apply list sym (str prefix index) label opts) m)))

(defn- gz-question-transformer
  [t-name m [sym type label text opts & choices]]
  (let [index (count m)]
    (cons (apply list sym type (str t-name "-" index) text (merge {:readable-label label} opts) 
                 (reduce (partial gz-choice-transformer type) '() choices)) 
          m)))

;; Warning a lot of mutable state follows
(defn find-next-reference
  [next all-tasks]
  (-> (filter #(= next (.question (.getValue %))) all-tasks)  
      first
      .getValue
      .getKey))

(defn update-next-reference
  [all-tasks [key task]]
  (when-let [next (.getNext task)]
    (println next)
    (let [new-ref (find-next-reference next all-tasks)]
      (.put all-tasks key (.withNext task new-ref))))
  (doseq [c (.choices task)]
    (when-let [next (.get c "next")]
      (println next)
      (.put c "next" (find-next-reference next all-tasks)))))

(defn- gz-next-references
  [tree]
  (doseq [t (.getTasks tree)] 
    (update-next-reference (.getTasks tree) t))
  tree)

(defmacro gz-tree
  [name & questions]
  (let [qs (reduce (partial gz-question-transformer name) '() questions)
        first-task (str name "-0")]
    `(gz-next-references (tree ~name ~first-task ~@qs))))

;; Safe Again...for now!
(def candels
  (gz-tree "candels"
           (question :single "Shape" "Is the galaxy simply smooth and rounded with no sign of a disk?" {}
                     (choice "Smooth" :next "How rounded is it?" :icon "smooth_round" :examples 1)
                     (choice "Features or disk" :next "Does the galaxy have a mostly clumpy appearance?" :icon "feature_clumpy" :examples 2)
                     (choice "Star or artifact" :next "Would you like to discuss this object?" :icon "star" :examples 3))

           (question :single "Round" "How rounded is it?" {:next "Is the galaxy currently merging or is there any sign of tidal debris?"}
                     (choice "Completely round" :icon "smooth_round" :examples 1)
                     (choice "In between" :icon "smooth_in-between" :examples 1)
                     (choice "Cigar shaped" :icon "smooth_cigar" :examples 1))

           (question :single "Clumps" "Does the galaxy have a mostly clumpy appearance?" {}
                     (choice "Yes" :next "How many clumps are there?" :icon "yes" :examples 1)
                     (choice "No" :next "Could this be a disk viewed edge-on?" :icon "no" :examples 1))

           (question :single "Clumps" "How many clumps are there?" {:next "Do the clumps appear in a straight line a chain or a cluster?"}
                     (choice "1" :next "Does the galaxy appear symmetrical?" :icon "clump_1")
                     (choice "2" :next "Is there one clump which is clearly brighter than the others?" :icon "clump_2")
                     (choice "3" :icon "clump_3")
                     (choice "4" :icon "clump_4")
                     (choice "More than 4" :icon "clump_4-plus")
                     (choice "Can't tell" :icon "clump_cant-tell"))

           (question :single "Clumps" "Do the clumps appear in a straight line a chain or a cluster?" {:next "Is there one clump which is clearly brighter than the others?"}
                     (choice "Straight Line" :icon "clump_line" :examples 2)
                     (choice "Chain" :icon "clump_chain" :examples 1)
                     (choice "Cluster / Irregular" :icon "clump_cluster" :examples 2)
                     (choice "Spiral" :icon "clump_spiral" :examples 1))

           (question :single "Clumps" "Is there one clump which is clearly brighter than the others?" {}
                     (choice "Yes" :next "Is the brightest clump central to the galaxy?" :icon "yes")
                     (choice "No" :next "Does the galaxy appear symmetrical?" :icon "no"))

           (question :single "Clumps" "Is the brightest clump central to the galaxy?" {}
                     (choice "Yes" :next "Does the galaxy appear symmetrical?" :icon "yes")
                     (choice "No" :next "Is the galaxy currently merging or is there any sign of tidal debris?" :icon "no"))

           (question :single "Symmetry" "Does the galaxy appear symmetrical?" {:next "Do the clumps appear to be embedded within a larger object?"}
                     (choice "Yes" :icon "yes")
                     (choice "No" :icon "no"))

           (question :single "Clumps" "Do the clumps appear to be embedded within a larger object?" {:next "Is the galaxy currently merging or is there any sign of tidal debris?"}
                     (choice "Yes" :icon "yes" :examples 1)
                     (choice "No" :icon "no" :examples 1))

           (question :single "Disk" "Could this be a disk viewed edge-on?" {}
                     (choice "Yes" :next "Does the galaxy have a bulge at its centre?" :icon "yes" :examples 1)
                     (choice "No" :next "Is there any sign of a bar feature through the centre of the galaxy?" :icon "no" :examples 1))

           (question :single "Bulge" "Does the galaxy have a bulge at its centre?" {:next "Is the galaxy currently merging or is there any sign of tidal debris?"}
                     (choice "Yes" :icon "yes" :examples 1)
                     (choice "No" :icon "no" :examples 1))

           (question :single "Bar" "Is there any sign of a bar feature through the centre of the galaxy?" {:next "Is there any sign of a spiral arm pattern?"}
                     (choice "Bar" :icon "yes" :examples 6)
                     (choice "No bar" :icon "no" :examples 1))

           (question :single "Spiral" "Is there any sign of a spiral arm pattern?" {}
                     (choice "Spiral" :next "How tightly wound do the spiral arms appear?" :icon "yes" :examples 1)
                     (choice "No spiral" :next "How prominent is the central bulge compared with the rest of the galaxy?" :icon "no" :examples 1))

           (question :single "Spiral" "How tightly wound do the spiral arms appear?" {:next "How many spiral arms are there?"}
                     (choice "Tight" :icon "spiral_tight" :examples 1)
                     (choice "Medium" :icon "spiral_medium" :examples 1)
                     (choice "Loose" :icon "spiral_loose" :examples 1))

           (question :single "Spiral" "How many spiral arms are there?" {:next "How prominent is the central bulge compared with the rest of the galaxy?"}
                     (choice "1" :icon "spiral_1")
                     (choice "2" :icon "spiral_2")
                     (choice "3" :icon "spiral_3")
                     (choice "4" :icon "spiral_4")
                     (choice "More than 4" :icon "spiral_4-plus")
                     (choice "Can't tell" :icon "spiral_cant-tell"))

           (question :single "Bulge" "How prominent is the central bulge compared with the rest of the galaxy?" {:next "Is the galaxy currently merging or is there any sign of tidal debris?"}
                     (choice "No bulge" :icon "bulge_none" :examples 1)
                     (choice "Obvious" :icon "bulge_obvious" :examples 1)
                     (choice "Dominant" :icon "bulge_dominant" :examples 1))

           (question :single "Merger" "Is the galaxy currently merging or is there any sign of tidal debris?" {:next "Would you like to discuss this object?"}
                     (choice "Merging" :icon "merger" :examples 4)
                     (choice "Tidal debris" :icon "tidal-debris" :examples 1)
                     (choice "Both" :icon "merger_tidal" :examples 2)
                     (choice "Neither" :icon "no" :examples 1))
           
           (question :single "Discuss" "Would you like to discuss this object?" {}
                     (choice "Yes" :icon "yes" :talk true)
                     (choice "No" :icon "no"))))

(def ferengi
  (gz-tree "ferengi"
           (question :single "Shape" "Is the galaxy simply smooth and rounded with no sign of a disk?" {}
                     (choice "Smooth" :next "How rounded is it?" :icon "smooth_round" :examples 1)
                     (choice "Features or disk" :next "Does the galaxy have a mostly clumpy appearance?" :icon "feature_clumpy" :examples 2)
                     (choice "Star or artifact" :next "Would you like to discuss this object?" :icon "star" :examples 0))

           (question :single "Round" "How rounded is it?" {:next "Is there anything odd?"}
                     (choice "Completely round" :icon "smooth_round" :examples 2)
                     (choice "In between" :icon "smooth_in-between" :examples 3)
                     (choice "Cigar shaped" :icon "smooth_cigar" :examples 2))

           (question :single "Clumps" "Does the galaxy have a mostly clumpy appearance?" {}
                     (choice "Yes" :next "How many clumps are there?" :icon "yes" :examples 0)
                     (choice "No" :next "Could this be a disk viewed edge-on?" :icon "no" :examples 0))

           (question :single "Clumps" "How many clumps are there?" {:next "Do the clumps appear in a straight line a chain or a cluster?"}
                     (choice "1" :next "Does the galaxy appear symmetrical?" :icon "clump_1" :examples 0)
                     (choice "2" :next "Is there one clump which is clearly brighter than the others?" :icon "clump_2" :examples 0)
                     (choice "3" :icon "clump_3" :examples 0)
                     (choice "4" :icon "clump_4" :examples 0)
                     (choice "More than 4" :icon "clump_4-plus" :examples 0)
                     (choice "Can't tell" :icon "clump_cant-tell" :examples 0))

           (question :single "Clumps" "Do the clumps appear in a straight line a chain or a cluster?" {:next "Is there one clump which is clearly brighter than the others?"}
                     (choice "Straight Line" :icon "clump_line" :examples 0)
                     (choice "Chain" :icon "clump_chain" :examples 0)
                     (choice "Cluster / Irregular" :icon "clump_cluster" :examples 0)
                     (choice "Spiral" :icon "clump_spiral" :examples 0))

           (question :single "Clumps" "Is there one clump which is clearly brighter than the others?" {}
                     (choice "Yes" :next "Is the brightest clump central to the galaxy?" :icon "yes" :examples 0)
                     (choice "No" :next "Does the galaxy appear symmetrical?" :icon "no" :examples 0))

           (question :single "Clumps" "Is the brightest clump central to the galaxy?" {}
                     (choice "Yes" :next "Does the galaxy appear symmetrical?" :icon "yes" :examples 0)
                     (choice "No" :next "Is there anything odd?" :icon "no" :examples 0))

           (question :single "Symmetry" "Does the galaxy appear symmetrical?" {:next "Do the clumps appear to be embedded within a larger object?"}
                     (choice "Yes" :icon "yes" :examples 0)
                     (choice "No" :icon "no" :examples 0))

           (question :single "Clumps" "Do the clumps appear to be embedded within a larger object?" {:next "Is there anything odd?"}
                     (choice "Yes" :icon "yes" :examples 0)
                     (choice "No" :icon "no" :examples 0))

           (question :single "Disk" "Could this be a disk viewed edge-on?" {}
                     (choice "Yes" :next "Does the galaxy have a bulge at its center? If so what shape?" :icon "yes" :examples 1)
                     (choice "No" :next "Is there any sign of a bar feature through the centre of the galaxy?" :icon "no" :examples 3))

           (question :single "Bulge" "Does the galaxy have a bulge at its center? If so what shape?" {:next "Is there anything odd?"}
                     (choice "Rounded" :icon "edge_round" :examples 2)
                     (choice "Boxy" :icon "edge_boxy" :examples 0)
                     (choice "No bulge" :icon "edge_none" :examples 3))

           (question :single "Bar" "Is there any sign of a bar feature through the centre of the galaxy?" {:next "Is there any sign of a spiral arm pattern?"}
                     (choice "Bar" :icon "yes" :examples 2)
                     (choice "No bar" :icon "no" :examples 3))

           (question :single "Spiral" "Is there any sign of a spiral arm pattern?" {}
                     (choice "Spiral" :next "How tightly wound do the spiral arms appear?" :icon "yes" :examples 3)
                     (choice "No spiral" :next "How prominent is the central bulge compared with the rest of the galaxy?" :icon "no" :examples 0))

           (question :single "Spiral" "How tightly wound do the spiral arms appear?" {:next "How many spiral arms are there?"}
                     (choice "Tight" :icon "spiral_tight" :examples 1)
                     (choice "Medium" :icon "spiral_medium" :examples 1)
                     (choice "Loose" :icon "spiral_loose" :examples 2))

           (question :single "Spiral" "How many spiral arms are there?" {:next "How prominent is the central bulge compared with the rest of the galaxy?"}
                     (choice "1" :icon "spiral_1" :examples 0)
                     (choice "2" :icon "spiral_2" :examples 2)
                     (choice "3" :icon "spiral_3" :examples 0)
                     (choice "4" :icon "spiral_4" :examples 0)
                     (choice "More than 4" :icon "spiral_4-plus" :examples 0)
                     (choice "Can't tell" :icon "spiral_cant-tell" :examples 2))

           (question :single "Bulge" "How prominent is the central bulge compared with the rest of the galaxy?" {:next "Is there anything odd?"}
                     (choice "No bulge" :icon "bulge_none" :examples 1)
                     (choice "Obvious" :icon "bulge_obvious" :examples 0)
                     (choice "Dominant" :icon "bulge_dominant" :examples 0))

           (question :single "Discuss" "Would you like to discuss this object?" {}
                     (choice "Yes" :icon "yes" :talk true)
                     (choice "No" :icon "no"))  

           (question :single "Odd" "Is there anything odd?" {}
                     (choice "Yes" :next "What are the odd features?" :icon "yes" :examples 1)
                     (choice "No" :next "Would you like to discuss this object?" :icon "no" :examples 3))

           (question :multi "Odd" "What are the odd features?" {}
                     (choice "Ring" :icon "ring" :examples 2)
                     (choice "Lens or arc" :icon "lens" :examples 0)
                     (choice "Disturbed" :icon "disturbed" :examples 0)
                     (choice "Irregular" :icon "irregular" :examples 0)
                     (choice "Other" :icon "other" :examples 0)
                     (choice "Merger" :icon "merger" :examples 1)
                     (choice "Dust lane" :icon "dustlane" :examples 1))))

(defn get-tree
  [tree]
  ({"ferengi" ferengi
    "candels" candels} tree))
