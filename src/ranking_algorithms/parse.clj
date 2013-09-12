(ns ranking-algorithms.parse
  (:use [net.cgrand.enlive-html]))

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

(defn matches [file]
  (->> file
       fetch-page
       extract-rows
       (map extract-content)
       (filter #(string? %))
       (filter #(not (clojure.string/blank? (re-find #"^[a-zA-Z0-9']+" %))))
       (partition-by #(re-find #"[a-zA-Z\s]+-[a-zA-Z\s]+ [0-9][\s]?.[\s]?[0-9]" %))
       (partition 2)
       (map vec)
       (map as-match)
       ))

(->> "/Users/markhneedham/code/ranking-algorithms/data/ec200203det.html"
     fetch-page
     extract-rows
     (map-indexed extract-content)
     (filter recognise-match?)
     (take 5))

(def all-matches
  (concat (matches "/Users/markhneedham/code/ranking-algorithms/data/ec200203det.html")
          [{:home "AC Milan", :away "Juventus Turijn", :home_score 0, :away_score 0 :round "Final"}]))

(def rounds
  (set (map #(:round %) all-matches)))

(defn extract-teams [matches]
  (->> matches
       (map #(:home %))
       set))

(defn extract-round [details]
  (nth (re-find #"([A-Za-z\- ]+) -" details) 1))

(defn as-match
  [[score details]]
  (let [match
        (first
         (re-seq #"([a-zA-Z\s]+)-([a-zA-Z\s]+) ([0-9])[\s]?.[\s]?([0-9])" (first score)))
        home-score (read-string (nth match 3))
        away-score (read-string (nth match 4))]
    {:home (cleanup (nth match 1))
     :away (cleanup (nth match 2))
     :home_score home-score
     :away_score away-score
     :round (if (and (= 0 home-score) (= 0 away-score))
              (extract-round (cleanup (nth details 0)))
              (extract-round (cleanup (nth details 1))) )}))

  (defn as-match
    [row]
    (let [match
          (first (re-seq #"([a-zA-Z\s]+)-([a-zA-Z\s]+) ([0-9])[\s]?.[\s]?([0-9])" row))]
      {:home (cleanup (nth match 1))
       :away (cleanup (nth match 2))
       :home_score (read-string (nth match 3))
       :away_score (read-string (nth match 4))}))

(defn cleanup [word]
  (clojure.string/join " "
                       (map clojure.string/capitalize
                            (clojure.string/split
                             (clojure.string/trim
                              (clojure.string/replace word "\n" " "))
                             #" "))))
(defn cleanup [word]
  (clojure.string/join " "
                       (map #(if (<= 3 (count %)) (clojure.string/capitalize %) %)
                                (filter #(not(clojure.string/blank? %))
                                        (clojure.string/split (clojure.string/replace word "\n" " ") #" ")))))


(clojure.string/join " "
                       (-> word
                           (clojure.string/replace "\n" " ")
                           (clojure.string/trim)
                           (clojure.string/split #" ")
                           (map clojure.string/capitalize)
                           ))





(def foo
  (->> "/Users/markhneedham/code/ranking-algorithms/data/ec200203det.html"
       fetch-page
       extract-rows
       (map extract-content)
       ))

(->> foo
     (filter #(string? %))
     (filter #(not (clojure.string/blank? (re-find #"^[a-zA-Z0-9']+" %))))
     (map #(re-find #"[a-zA-Z\s]+-[a-zA-Z\s]+ [0-9][\s]?.[\s]?[0-9]" %)))

(->> foo
     (filter #(string? %))
     (filter #(not (clojure.string/blank? (re-find #"^[a-zA-Z0-9']+" %))))
     (partition-by #(re-find #"[a-zA-Z\s]+-[a-zA-Z\s]+ [0-9][\s]?.[\s]?[0-9]" %))
     (partition 2))

(defn recognise-match? [row]
  (and (string? row) (re-matches #"[a-zA-Z\s]+-[a-zA-Z\s]+ [0-9][\s]?.[\s]?[0-9]" row)))

(defn recognise-match? [[idx row]]
  (and (string? row) (re-matches #"[a-zA-Z\s]+-[a-zA-Z\s]+ [0-9][\s]?.[\s]?[0-9]" row)))

(defn extract-rows [page]
  (select page [:div.Section1 :p :span]))

(defn extract-content [row]
  (first (get row :content)))

(defn extract-content [idx row]
  [idx (first (get row :content))])

(defn find-team [team teams]
  (first
   (filter #(= team (:name %)) teams)))

(def m (select page [:div.Section1 :p]))

(defn extract-finalists [page]
  (filter (fn [p] (not (empty? (select p [:b])))) page))

(map (fn [row] (map #(:content % ) (select row [:b]))) (extract-finalists m))

(map #(:content %)
     (first (nth (map (fn [row] (map #(:content % ) (select row [:b]))) (extract-finalists m)) 6)))

(nth (map (fn [row] (map #(:content % ) (select row [:b]))) (extract-finalists m)) 6)

(def blah  (nth (->> (extract-finalists m)
                     (map (fn [row] (map #(:content % ) (select row [:b]))))
                     ) 6))


(comment  (def bar  (->> foo
                         (filter (fn [row] (string? row)))
                         (partition-by (fn [row] (clojure.string/blank? (re-find #"[a-zA-Z\s0-9']+" row)))))))

