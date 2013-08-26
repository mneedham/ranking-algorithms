(ns ranking-algorithms.core-test
  (:use clojure.test
        ranking-algorithms.core))

(deftest pairs-of-values
   (let [args ["--server" "localhost"
               "--port" "8080"
               "--environment" "production"]]
      (is (= {:server "localhost"
              :port "8080"
              :environment "production"}
             (parse-args args)))))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))
