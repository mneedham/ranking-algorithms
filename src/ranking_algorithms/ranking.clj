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

(defn process-match [ts match]
  (let [{:keys [home away home_score away_score]} match]
    (cond
     (> home_score away_score)
     (-> ts
         (update-in  [home :points]
                     #(ranking-after-win {:ranking % :opponent-ranking (:points (get ts away)) :importance 32}))
         (update-in  [away :points]
                     #(ranking-after-loss {:ranking % :opponent-ranking (:points (get ts home)) :importance 32}))) 
     (> away_score home_score)
     (-> ts
         (update-in  [home :points]
                     #(ranking-after-loss {:ranking % :opponent-ranking (:points  (get ts away)) :importance 32}))
         (update-in  [away :points]
                     #(ranking-after-win {:ranking % :opponent-ranking (:points (get ts home)) :importance 32})))
     (= home_score away_score) ts)))


