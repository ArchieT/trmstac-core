(ns archiet.trm.stac.core-test
  (:require [clojure.test :refer :all]
            [archiet.trm.stac.core :refer :all]
            [clj-http.client :as client]))

(deftest downloading
  (testing "Downloading"
    (is (= 0 0))
    (let [got (client/get url)
          got (:body got)]
      (println (prn-str (stations got))))))
