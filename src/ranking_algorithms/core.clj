(ns ranking-algorithms.core
  (:require [ranking-algorithms.ranking :as ranking])
  (:require [ranking-algorithms.parse :as parse])
  (:require [ranking-algorithms.uefa :as uefa]))

(defn top-teams [number matches]
  (let [teams-with-rankings (ranking/initial-rankings (uefa/extract-teams matches))]
    (map (fn [[ team details]]  [team (format "%.2f" ( :points details))])
         (take number
               (sort-by #(:points (val %))
                        >
                        (reduce ranking/process-match teams-with-rankings matches))))))

(defn print-top-teams [number all-matches]
  (doseq [[team details] (ranking-algorithms.core/top-teams number all-matches)]
    (println team
             details
             (match-record (show-matches team all-matches))
             (performance (show-matches team all-matches)))))

(defn match-record [opponents]
  {:wins   (count (filter #(> (:for %) (:against %)) opponents))
   :draw   (count (filter #(= (:for %) (:against %)) opponents))
   :loses  (count (filter #(< (:for %) (:against %)) opponents))})

(defn performance [opponents]
  (let [last-match (last opponents)]
    (:round last-match)))

(defn show-opposition [team match]
  (if (= team (:home match))
    {:opposition (:away match) :for (:home_score match)
     :against (:away_score match) :round (:round match)}
    {:opposition (:home match) :for (:away_score match)
     :against (:home_score match) :round (:round match)}))

(defn show-matches [team matches]
  (->> matches
       (filter #(or (= team (:home %)) (= team (:away %))))
       (map #(show-opposition team %))))

(ranking-algorithms.core/match-record
 (ranking-algorithms.core/show-matches "FC Liverpool" all-matches))

(doseq [match (show-matches "Manchester United" all-matches)]
  (println match))







