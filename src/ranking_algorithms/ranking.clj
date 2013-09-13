(ns ranking-algorithms.ranking
  (:require [clojure.math.numeric-tower :as math]))

(defn expected [my-ranking opponent-ranking]
  (/ 1.0
     (+ 1 (math/expt 10 (/ (- opponent-ranking my-ranking) 400)))))

(defn ranking-after-win
  [{ ranking :ranking opponent-ranking :opponent-ranking importance :importance}]
  (+ ranking (* importance (- 1 (expected ranking opponent-ranking) ))))

(defn ranking-after-loss
  [{ ranking :ranking opponent-ranking :opponent-ranking importance :importance}]
  (+ ranking (* importance (- 0 (expected ranking opponent-ranking) ))))

(comment (def round-value
           {"First Qualifying Round" 4
            "Second qualifying round" 6
            "Third qualifying round" 8
            "First group-match stage" 12
            "Second group-match stage" 15   
            "Quarter Finals" 18
            "Semi-finals" 25
            "Final" 32}))

(def round-value
  {"First qualifying round" 2
   "Second qualifying round" 3
   "Third qualifying round" 5
   "Play-offs" 7
   "Group stage" 12
   "Round of 16" 15
   "Quarter-finals" 18
   "Semi-finals" 25
   "Final" 32 })

(def round-value
  {})

(defn process-match [ts match]
  (let [{:keys [home away home_score away_score round]} match]

    (cond
     (> home_score away_score)
     (-> ts
         (update-in  [home :points]
                     #(ranking-after-win {:ranking % :opponent-ranking (:points (get ts away)) :importance (get round-value round 32)}))
         (update-in  [away :points]
                     #(ranking-after-loss {:ranking % :opponent-ranking (:points (get ts home)) :importance (get round-value round 32)}))) 
     (> away_score home_score)
     (-> ts
         (update-in  [home :points]
                     #(ranking-after-loss {:ranking % :opponent-ranking (:points  (get ts away)) :importance (get round-value round 32)}))
         (update-in  [away :points]
                     #(ranking-after-win {:ranking % :opponent-ranking (:points (get ts home)) :importance (get round-value round 32)})))
     (= home_score away_score) ts)))


