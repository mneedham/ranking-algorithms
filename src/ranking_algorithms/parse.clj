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
  (apply array-map (mapcat (fn [x] [x {:points 1200}]) (extract-teams all-matches))))

(defn top-teams [number]
  (take number
        (sort-by (fn [x] (:points (val x)))
                 >
                 (seq (reduce process-match starting-teams all-matches)))))

(defn show-matches [team]
  (->> all-matches
       (filter #(or (= team (:home %)) (= team (:away %))))
       (map #(show-opposition team %))))

(defn show-opposition [team match]
  (if (= team (:home match))
    {:opposition (:away match) :score (str (:home_score match) "-" (:away_score match))}
    {:opposition (:home match) :score (str (:away_score match) "-" (:home_score match))}))

(defn process-match [ts match]
  (let [{:keys [home away home_score away_score]} match]
    (cond
     (> home_score away_score)
     (-> ts
         (update-in  [home :points]
                     (fn [points] (core/ranking-after-win {:ranking points
                                                          :opponent-ranking (:points (get ts away))
                                                          :importance 32})))
         (update-in  [away :points]
                     (fn [points]  
                       (core/ranking-after-loss {:ranking points
                                                 :opponent-ranking (:points (get ts home))
                                                 :importance 32})))) 
     (> away_score home_score)
     (-> ts
      (update-in  [home :points]
                 (fn [points] (core/ranking-after-loss {:ranking points
                                                       :opponent-ranking (:points  (get ts away))
                                                       :importance 32})))
      (update-in  [away :points]
                 (fn [points] (core/ranking-after-win {:ranking points
                                                      :opponent-ranking (:points (get ts home))
                                                      :importance 32}))))
     (= home_score away_score) ts)))

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

(defn find-team [team teams]
  (first
   (filter #(= team (:name %)) teams)))
