(ns ranking-algorithms.core
  (:require [ranking-algorithms.ranking :as ranking])
  (:require [ranking-algorithms.parse :as parse])
  (:require [ranking-algorithms.uefa :as uefa]))

(defn merge-rankings [base-rankings initial-rankings]
  (merge initial-rankings
         (into {} (filter #(contains? initial-rankings (key %)) base-rankings))))

(defn rank-teams
  ([matches] (rank-teams matches {}))
  ([matches base-rankings]
     (let [teams-with-rankings
           (merge-rankings base-rankings (ranking/initial-rankings (uefa/extract-teams matches)))]       
       (map (fn [[ team details]]  [team (read-string (format "%.2f" (:points details)))])         
            (sort-by #(:points (val %))
                     >
                     (reduce ranking/process-match teams-with-rankings matches))))))

(defn top-teams
  ([number matches] (top-teams number matches {}))
  ([number matches base-rankings]
      (take number (rank-teams matches base-rankings))))

(defn base-ratings [teams]
  (apply array-map (flatten (map (fn [[team points]] [team {:points points}]) teams))))

(def base
  (base-ratings (rank-teams (ranking-algorithms.uefa/every-match))))

(defn format-for-printing [all-matches idx [team ranking]]
  (let [team-matches (show-matches team all-matches)]
    (merge  {:rank (inc idx) :team team :ranking ranking :round (performance team-matches)}
            (match-record team-matches))))

(defn print-top-teams
  ([number all-matches] (print-top-teams number all-matches {}))
  ([number all-matches base-rankings]
      (clojure.pprint/print-table
       [:rank :team :ranking :round :wins :draw :loses]
       (map-indexed (partial format-for-printing all-matches) (top-teams number all-matches base-rankings)))))

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

(comment (defn print-top-teams [number all-matches]
           (doseq [[team details] (ranking-algorithms.core/top-teams number all-matches)]
             (println team
                      details
                      (match-record (show-matches team all-matches))
                      (performance (show-matches team all-matches))))))







