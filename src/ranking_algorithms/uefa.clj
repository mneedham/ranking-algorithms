(ns ranking-algorithms.uefa
  (:use [net.cgrand.enlive-html])
  (:require [clj-time.core :as time])
  (:require [clj-time.format :as f])
  (:require [clojure.data.json :as json])
  (:require [clojure.java.io :as io]))

(defn fetch-page
  [file-path]
  (html-resource
   (java.io.StringReader. (slurp file-path))))

(defn extract-rows [page]
  (select page [:table :tbody]))

(defn cleanup [field]
  (clojure.string/trim field))

(defn as-date [date-field]
  (f/parse (f/formatter "dd MMM YYYY") date-field ))

(defn as-date-string [date]
  (f/unparse (f/formatter "dd MMM YYYY") date))

(defn date-aware-value-reader [key value]
  (if (= key :date)
    (as-date value)
    value))

(defn date-aware-value-writer [key value]
  (if (= key :date)
    (as-date-string value)
    value))

(defn write-to-file [matches file]
  (spit file (json/write-str matches :value-fn date-aware-value-writer)))

(defn read-from-file [file]
  (json/read-str (slurp file)
                 :value-fn date-aware-value-reader
                 :key-fn keyword))

(comment  (defn read-from-file [file]
            (map #(string-keys-to-symbols (update-in % ["date"] as-date))
                 (json/read-str (slurp file)))))

(comment (defn string-keys-to-symbols [map]
           (reduce #(assoc %1 (-> (key %2) keyword) (val %2)) {} map)))

(comment (defn write-to-file [matches file]
           (with-open [wrtr (io/writer file)]
             (.write wrtr (json/write-str (json-friendly matches))))))

(comment  (defn json-friendly [matches]
            (map #(update-in % [:date] as-date-string) matches)))

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

(defn files [year]
  (map #(str "data/uefa/" year "/_matchesbydate.html." %)
       (range (- ( count (file-seq (clojure.java.io/file (str "data/uefa/" year)))) 1))))

(defn all-matches [year] 
  (sort-by :date
           (mapcat (fn [file] (->> file fetch-page extract-rows (map extract-content)))
                   (files year))))

(defn every-match []
  (mapcat #(all-matches %) (range 2004 2013)))

(def seasons [2004 2005 2006 2008 2009 2010 2011 2012 2013])

(defn extract-teams [matches]
  (->> matches
       (mapcat (fn [match] [(:home match) (:away match)]))
       set))

(defn teams-by-season []
  (map #(extract-teams (all-matches %))
       [2004 2005 2006 2008 2009 2010 2011 2012]))


(defn every-team []
  (apply array-map
         (mapcat (fn [team] [team 0])
                 (set (mapcat #(extract-teams (all-matches %)) (range 2004 2013))))))

(defn periods-missed [every-team teams-by-season]
  (reductions (fn [all-teams season-teams]
                (reduce (fn [at t] (if (contains? at t) (update-in at [t] (fn [x] 1)) at))
                        (reduce (fn [at [t _]] (update-in at [t] inc)) all-teams all-teams)
                        season-teams ))
              every-team
              teams-by-season))

(def periods-missed-per-season
  (zipmap seasons (periods-missed (every-team) (teams-by-season))))

 



(defn rounds []
  (map #(:round %)
       (sort-by :date
                (mapcat (fn [file]
                          (->> file fetch-page extract-rows (map extract-content)))
                        files))))

(comment

  (set
   (map #(:round %)
        (mapcat (fn [file] (->> file fetch-page extract-rows (map extract-content))) files))))

(comment (doseq [match (map extract-content (extract-rows page))]
           (println (:home match) "vs " (:away match) (:score match) (:round match))))



