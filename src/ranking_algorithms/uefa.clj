(ns ranking-algorithms.uefa
  (:use [net.cgrand.enlive-html])
  (:require [clj-time.core :as time]))

(defn fetch-page
  "Processes a file path into a HTML resource"
  [file-path]
  (html-resource (java.io.StringReader. (slurp file-path))))

(defn extract-rows [page]
  (select page [:table :tbody]))

(defn extract-content [match]
  (let [score (cleanup (first (:content (first  (select match [:tr :td.score :a])))))
        home (cleanup (first (:content (first  (select match [:tr :td.home :a])))))       
        away (cleanup (first (:content (first  (select match [:tr :td.away :a])))))
        round (cleanup (first (:content (first (select match [:span.rname :a])))))
        date (as-date (cleanup (first (:content (first (select match [:span.dateT]))))))]
    {:home home
     :away away
     :home_score (read-string (nth (clojure.string/split score #"-") 0))
     :away_score (read-string (nth (clojure.string/split score #"-") 1))
     :round round
     :date date}))

(defn all-matches [year] 
  (sort-by :date
           (mapcat (fn [file] (->> file fetch-page extract-rows (map extract-content)))
                   (files year))))

(def every-match
  (mapcat #(all-matches %) (range 2004 2014)))

(defn files [year]
  (map #(str "data/uefa/" year "/_matchesbydate.html." %)
       (range (- ( count (file-seq (clojure.java.io/file (str "data/uefa/" year)))) 1))))

(defn extract-teams [matches]
  (->> matches
       (mapcat (fn [match] [(:home match) (:away match)]))
       set))

(defn as-date [date-field]
  (parse (formatter "dd MMM YYYY") date-field ))

(defn rounds []
  (map #(:round %)
       (sort-by :date
                (mapcat (fn [file]
                          (->> file fetch-page extract-rows (map extract-content)))
                        files))))

(defn cleanup [field]
  (clojure.string/trim field))

(comment

  (set
   (map #(:round %)
        (mapcat (fn [file] (->> file fetch-page extract-rows (map extract-content))) files))))

(comment (doseq [match (map extract-content (extract-rows page))]
           (println (:home match) "vs " (:away match) (:score match) (:round match))))



