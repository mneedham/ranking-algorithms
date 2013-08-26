(ns ranking-algorithms.parse
  (:use [net.cgrand.enlive-html]))

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

(defn teams [matches]
  (->> matches
       (map #(get % :home))
       set))

(defn as-match
  [row]
  (let [match
        (first (re-seq #"([a-zA-Z\s]+)-([a-zA-Z\s]+) ([0-9])[\s]?.[\s]?([0-9])" row))]
    {:home (cleanup (nth match 1)) :away (cleanup (nth match 2))
     :home_score (nth match 3) :away_score (nth match 4)}))

(defn cleanup [word]
  (clojure.string/replace word "\n" " "))

(defn recognise-match? [row]
  (and (string? row) (re-matches #"[a-zA-Z\s]+-[a-zA-Z\s]+ [0-9][\s]?.[\s]?[0-9]" row)))

(defn extract-rows [page]
  (select page [:div.Section1 :p :span]))

(defn extract-content [row]
  (first (get row :content)))

