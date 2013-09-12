(ns ranking-algorithms.core
  (:require [ranking-algorithms.ranking :as ranking])
  (:require [ranking-algorithms.parse :as parse]))

(defn top-teams [number matches]
  (let [teams-with-rankings
        (apply array-map (mapcat (fn [x] [x {:points 1200}]) (parse/extract-teams matches)))]
    (take number
          (sort-by (fn [x] (:points (val x)))
                   >
                   (seq (reduce ranking/process-match teams-with-rankings matches))))))

(doseq [[team details] (ranking-algorithms.core/top-teams 10 all-matches)]
  (println team (:points details)))

(ranking-algorithms.core/match-record
 (ranking-algorithms.core/show-matches "FC Liverpool" all-matches))

(doseq [match (show-matches "Manchester United" all-matches)]
  (println match))

(defn show-opposition [team match]
  (if (= team (:home match))
    {:opposition (:away match) :for (:home_score match) :against (:away_score match)}
    {:opposition (:home match) :for (:away_score match) :against (:home_score match)}))

(defn match-record [opponents]
  {:wins   (count (filter #(> (:for %) (:against %)) opponents))
   :draw   (count (filter #(= (:for %) (:against %)) opponents))
   :loses  (count (filter #(< (:for %) (:against %)) opponents))})

(defn show-matches [team matches]
  (->> matches
       (filter #(or (= team (:home %)) (= team (:away %))))
       (map #(show-opposition team %))))

