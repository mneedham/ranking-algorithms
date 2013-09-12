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
    {:score score :home home :away away :round round :date date}))

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

(doseq [match (map extract-content (extract-rows page))]
  (println (:home match) "vs " (:away match) (:score match) (:round match)))

(def files
  (map #(str "data/uefa/_matchesbydate.html." %) (range 38)))



