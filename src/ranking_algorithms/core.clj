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

(defn show-opposition [team match]
  (if (= team (:home match))
    {:opposition (:away match) :score (str (:home_score match) "-" (:away_score match))}
    {:opposition (:home match) :score (str (:away_score match) "-" (:home_score match))}))

(defn show-matches [team matches]
  (->> matches
       (filter #(or (= team (:home %)) (= team (:away %))))
       (map #(show-opposition team %))))

