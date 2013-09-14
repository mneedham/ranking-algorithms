(ns ranking-algorithms.glicko
  (:require [clojure.math.numeric-tower :as math]))

(defn initial-rankings [teams]
  (apply array-map (mapcat (fn [x] [x {:points 1500 :rd 350}]) teams)))

(def q
  (/ (java.lang.Math/log 10) 400))

(defn g [rd]
  (/ 1
     (java.lang.Math/sqrt (+ 1
                             (/ (* 3 (math/expt q 2) (math/expt rd 2))
                                (math/expt ( . Math PI) 2))))))

(defn e [rating opponent-rating opponent-rd]
  (/ 1
     (+ 1
        (math/expt 10 (/ (* (- (g opponent-rd))
                            (- rating opponent-rating))
                         400)))))
(defn d2 [opponents]
  (/ 1  (* (math/expt q 2)
           (reduce process-opponent 0 opponents))))

(defn process-opponent [total opponent]
  (let [{:keys [g e]} opponent]
    (+ total
       (* (math/expt g 2) e (- 1 e)))))

(defn update-ranking [ranking-delta opponent]
  (let [{:keys [ranking opponent-ranking opponent-ranking-rd score]} opponent]
    (+ ranking-delta
       (* (g opponent-ranking-rd)
          (- score (e ranking opponent-ranking opponent-ranking-rd))))))

(defn g-and-e
  [ranking {o-rd :opponent-ranking-rd o-ranking :opponent-ranking}]
  {:g (g o-rd) :e (e ranking o-ranking o-rd)})

(defn ranking-after-round
  [{ ranking :ranking rd :ranking-rd opponents :opponents}]  
  (+ ranking
     (* (/ q
           (+ (/ 1 (math/expt rd 2))
              (/ 1 (d2 (map (partial g-and-e ranking) opponents)))))
        (reduce update-ranking 0 (map #(assoc-in % [:ranking] ranking) opponents)))))

(defn rd-after-round
  [{ ranking :ranking rd :ranking-rd opponents :opponents}]
  (java.lang.Math/sqrt (/ 1 (+ (/ 1 (math/expt rd 2)
                                  )
                               (/ 1 (d2 (map (partial g-and-e ranking) opponents)))))))

(defn process-match [ts match]
  (let [{:keys [home away home_score away_score]} match]
    (-> ts
        (update-in [home :points]
                   #(ranking-after-round {:ranking %
                                          :ranking-rd (:rd (get ts home))
                                          :opponent-ranking (:points (get ts away))
                                          :opponent-ranking-td (:rd (get ts away))})))))
