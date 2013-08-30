(ns ranking-algorithms.parse
  (:use [net.cgrand.enlive-html])
  (:require [ranking-algorithms.core :as core]))

(defn fetch-page
  "Downloads a document as an html-resource"
  [file-path]
  (html-resource (java.io.StringReader. (slurp file-path))))

(extract-content (first (select (fetch-url "/tmp/football/ec200203det.html") [:div.Section1 :p :span])))

(defn matches [file]
  (->> file
       fetch-page
       extract-rows
       (map extract-content)
       (filter recognise-match?)
       (map as-match)))

(def all-matches (matches "/tmp/football/ec200203det.html"))

(defn extract-teams [matches]
  (->> matches
       (map #(get % :home))
       set)) 

(def starting-teams
  (map (fn [x] {:name x :points 1200})
       (seq (extract-teams all-matches))))

(defn as-match
  [row]
  (let [match
        (first (re-seq #"([a-zA-Z\s]+)-([a-zA-Z\s]+) ([0-9])[\s]?.[\s]?([0-9])" row))]
    {:home (cleanup (nth match 1))
     :away (cleanup (nth match 2))
     :home_score (read-string (nth match 3))
     :away_score (read-string (nth match 4))}))

(defn cleanup [word]
  (clojure.string/trim
   (clojure.string/replace word "\n" " ")))

(defn recognise-match? [row]
  (and (string? row) (re-matches #"[a-zA-Z\s]+-[a-zA-Z\s]+ [0-9][\s]?.[\s]?[0-9]" row)))

(defn extract-rows [page]
  (select page [:div.Section1 :p :span]))

(defn extract-content [row]
  (first (get row :content)))

(def teams
  [ {:name "Manchester United" :points 1200}
    {:name "Manchester City" :points 1200} ])

(def t
  (atom teams))
(def m
  [{:home "Manchester United", :away "Manchester City", :home_score 1, :away_score 0}
   {:home "Manchester United", :away "Manchester City", :home_score 2, :away_score 0}])

(map (fn [match]
       (swap! t (fn [teams]
                  (update-teams teams
                               (:home match)
                               (new-home-score match teams)
                               (:away match)
                               (new-away-score match teams)))))
     m)

(defn new-home-score
  [match teams]
  (let [home-team (find-team (:home match) teams)]
    (inc (:points home-team))))

(defn new-away-score
  [match teams]
  (let [away-team (find-team (:away match) teams)]
    (inc (:points away-team))))

(defn update-teams
  [teams team1 new-score1 team2 new-score2]
  (vec
   (map #(cond (= team1 (:name %)) (assoc % :points new-score1)
               (= team2 (:name %)) (assoc % :points new-score2)
               :else %)
        teams)))

(map #(if (= "Manchester United" (:name %))
        (assoc % :points 1500)
        %)
     teams)

(swap! t
       (fn [teams]
         (map #(if (= "Manchester United" (:name %)) (assoc % :points 1500) %)
              teams)))

(defn new-home-score
  [match teams]
  (let [home-team (find-team (:home match) teams)
        away-team (find-team (:away match) teams)]
    (cond (> (:home_score match) (:away_score match))
          (core/ranking-after-win {:ranking (:points home-team)
                                   :opponent-ranking (:points away-team)
                                   :importance 32} )
          (< (:home_score match) (:away_score match))
          (core/ranking-after-loss {:ranking (:points home-team)
                                   :opponent-ranking (:points away-team)
                                   :importance 32} )
          :else
          (:points home-team))))

(defn new-away-score
  [match teams]
  (let [home-team (find-team (:home match) teams)
        away-team (find-team (:away match) teams)]
    (cond (> (:away_score match) (:home_score match))
          (core/ranking-after-win {:ranking (:points away-team)
                                   :opponent-ranking (:points home-team)
                                   :importance 32} )
          (< (:away_score match) (:home_score match))
          (core/ranking-after-loss {:ranking (:points away-team)
                                   :opponent-ranking (:points home-team)
                                   :importance 32} )
          :else
          (:points away-team))))

(swap! teams-atom (fn [teams] (update-team @teams-atom "Manchester United" 1300)))


(map (fn [match]
       (swap! teams-atom
              (fn [teams] (update-team @teams-atom (:home match) 1300))))
     all-matches)



(defn find-team [team teams]
  (first
   (filter #(= team (:name %)) teams)))
