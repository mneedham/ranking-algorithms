(ns ranking-algorithms.parse
  (:use [net.cgrand.enlive-html])
  (:require [ranking-algorithms.core :as core]))

(defn fetch-page
  "Processes a file path into a HTML resource"
  [file-path]
  (html-resource (java.io.StringReader. (slurp file-path))))

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
       (map #(:home %))
       set))

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
