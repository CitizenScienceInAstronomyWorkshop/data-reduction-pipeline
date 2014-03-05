  (question "Shape" "Is the galaxy simply smooth and rounded with no sign of a disk?"
    (answer "Smooth" :leadsTo "How rounded is it?" :icon "smooth_round" :examples 1)
    (answer "Features or disk" :leadsTo "Does the galaxy have a mostly clumpy appearance?" :icon "feature_clumpy" :examples 2)
    (answer "Star or artifact" :leadsTo "Would you like to discuss this object?" :icon "star" :examples 0))
  
  (question "Round" "How rounded is it?" :leadsTo "Is there anything odd?"
    (answer "Completely round" :icon "smooth_round" :examples 2)
    (answer "In between" :icon "smooth_in-between" :examples 3)
    (answer "Cigar shaped" :icon "smooth_cigar" :examples 2))
  
  (question "Clumps" "Does the galaxy have a mostly clumpy appearance?"
    (answer "Yes" :leadsTo "How many clumps are there?" :icon "yes" :examples 0)
    (answer "No" :leadsTo "Could this be a disk viewed edge-on?" :icon "no" :examples 0))
  
  (question "Clumps" "How many clumps are there?" :leadsTo "Do the clumps appear in a straight line a chain or a cluster?"
    (answer "1" :leadsTo "Does the galaxy appear symmetrical?" :icon "clump_1" :examples 0)
    (answer "2" :leadsTo "Is there one clump which is clearly brighter than the others?" :icon "clump_2" :examples 0)
    (answer "3" :icon "clump_3" :examples 0)
    (answer "4" :icon "clump_4" :examples 0)
    (answer "More than 4" :icon "clump_4-plus" :examples 0)
    (answer "Can"t tell" :icon "clump_cant-tell" :examples 0))
  
  (question "Clumps" "Do the clumps appear in a straight line a chain or a cluster?" :leadsTo "Is there one clump which is clearly brighter than the others?"
    (answer "Straight Line" :icon "clump_line" :examples 0)
    (answer "Chain" :icon "clump_chain" :examples 0)
    (answer "Cluster / Irregular" :icon "clump_cluster" :examples 0)
    (answer "Spiral" :icon "clump_spiral" :examples 0))
  
  (question "Clumps" "Is there one clump which is clearly brighter than the others?"
    (answer "Yes" :leadsTo "Is the brightest clump central to the galaxy?" :icon "yes" :examples 0)
    (answer "No" :leadsTo "Does the galaxy appear symmetrical?" :icon "no" :examples 0))
  
  (question "Clumps" "Is the brightest clump central to the galaxy?"
    (answer "Yes" :leadsTo "Does the galaxy appear symmetrical?" :icon "yes" :examples 0)
    (answer "No" :leadsTo "Is there anything odd?" :icon "no" :examples 0))
  
  (question "Symmetry" "Does the galaxy appear symmetrical?" :leadsTo "Do the clumps appear to be embedded within a larger object?"
    (answer "Yes" :icon "yes" :examples 0)
    (answer "No" :icon "no" :examples 0))
  
  (question "Clumps" "Do the clumps appear to be embedded within a larger object?" :leadsTo "Is there anything odd?"
    (answer "Yes" :icon "yes" :examples 0)
    (answer "No" :icon "no" :examples 0))
  
  (question "Disk" "Could this be a disk viewed edge-on?"
    (answer "Yes" :leadsTo "Does the galaxy have a bulge at its center? If so what shape?" :icon "yes" :examples 1)
    (answer "No" :leadsTo "Is there any sign of a bar feature through the centre of the galaxy?" :icon "no" :examples 3))
  
  (question "Bulge" "Does the galaxy have a bulge at its center? If so what shape?" :leadsTo "Is there anything odd?"
    (answer "Rounded" :icon "edge_round" :examples 2)
    (answer "Boxy" :icon "edge_boxy" :examples 0)
    (answer "No bulge" :icon "edge_none" :examples 3))
  
  (question "Bar" "Is there any sign of a bar feature through the centre of the galaxy?" :leadsTo "Is there any sign of a spiral arm pattern?"
    (answer "Bar" :icon "yes" :examples 2)
    (answer "No bar" :icon "no" :examples 3))
  
  (question "Spiral" "Is there any sign of a spiral arm pattern?"
    (answer "Spiral" :leadsTo "How tightly wound do the spiral arms appear?" :icon "yes" :examples 3)
    (answer "No spiral" :leadsTo "How prominent is the central bulge compared with the rest of the galaxy?" :icon "no" :examples 0))
  
  (question "Spiral" "How tightly wound do the spiral arms appear?" :leadsTo "How many spiral arms are there?"
    (answer "Tight" :icon "spiral_tight" :examples 1)
    (answer "Medium" :icon "spiral_medium" :examples 1)
    (answer "Loose" :icon "spiral_loose" :examples 2))
  
  (question "Spiral" "How many spiral arms are there?" :leadsTo "How prominent is the central bulge compared with the rest of the galaxy?"
    (answer "1" :icon "spiral_1" :examples 0)
    (answer "2" :icon "spiral_2" :examples 2)
    (answer "3" :icon "spiral_3" :examples 0)
    (answer "4" :icon "spiral_4" :examples 0)
    (answer "More than 4" :icon "spiral_4-plus" :examples 0)
    (answer "Can"t tell" :icon "spiral_cant-tell" :examples 2))
  
  (question "Bulge" "How prominent is the central bulge compared with the rest of the galaxy?" :leadsTo "Is there anything odd?"
    (answer "No bulge" :icon "bulge_none" :examples 1)
    (answer "Obvious" :icon "bulge_obvious" :examples 0)
    (answer "Dominant" :icon "bulge_dominant" :examples 0))
  
  (question "Discuss" "Would you like to discuss this object?"
    (answer "Yes" :icon "yes" talk: true :examples 0)
    (answer "No" :icon "no" :examples 0))
  
  (question "Odd" "Is there anything odd?"
    (answer "Yes" :leadsTo "What are the odd features?" :icon "yes" :examples 1)
    (answer "No" :leadsTo "Would you like to discuss this object?" :icon "no" :examples 3))
  
  (question "Odd" "What are the odd features?"
    (checkbox "Ring" :icon "ring" :examples 2)
    (checkbox "Lens or arc" :icon "lens" :examples 0)
    (checkbox "Disturbed" :icon "disturbed" :examples 0)
    (checkbox "Irregular" :icon "irregular" :examples 0)
    (checkbox "Other" :icon "other" :examples 0)
    (checkbox "Merger" :icon "merger" :examples 1)
    (checkbox "Dust lane" :icon "dustlane" :examples 1))
