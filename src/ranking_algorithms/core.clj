(ns ranking-algorithms.core
  (:require [clojure.math.numeric-tower :as math]))


(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn parse-args [args]
  "mark")


(comment  (defn ranking-after-win [opts]
            (let [ranking (get opts :ranking)
                  expected (/ 1.0
                              (+ 1 (math/expt 10 (/ (- ranking (get opts :opponent-ranking)) 400))))]
              (+ ranking
                 (* (get opts :importance) (- 1 expected))))))

(defn ranking-after-win
  [{ ranking :ranking opponent-ranking :opponent-ranking importance :importance}]
  (+ ranking (* importance (- 1 (expected ranking opponent-ranking) ))))

(defn ranking-after-loss
  [{ ranking :ranking opponent-ranking :opponent-ranking importance :importance}]
  (+ ranking (* importance (- 0 (expected ranking opponent-ranking) ))))

(defn expected [my-ranking opponent-ranking]
  (/ 1.0
     (+ 1 (math/expt 10 (/ (- opponent-ranking my-ranking) 400)))))
