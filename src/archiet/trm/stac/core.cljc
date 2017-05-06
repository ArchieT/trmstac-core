(ns archiet.trm.stac.core
  (:require [schema.core :as sh]
            #?(:clj [clojure.spec :as s]
               :cljs [cljs.spec :as s])
            #?(:cljs [cljs.reader :refer [read-string]])
            [clojure.set :as set]))

(defonce counts-locations-regex
  #"Stacja nr ? (\d{1,2}) {0,5}<.br> {0,6}Dostępne rowery: (\d{1,2}) {0,4}<.br> {0,4}Wolne sloty (\d{1,2}) \x27, (\d+\.\d+) , (\d+\.\d+) , 'http")
(defn counts-location-from-grouped-match [m]
  {:station-num (read-string (nth m 1))
   :available-bikes (read-string (nth m 2))
   :free-slots (read-string (nth m 3))
   :station-location [(read-string (nth m 4)) (read-string (nth m 5))]})
(defn counts-locations-from-seq-grouped [s]
  (map counts-location-from-grouped-match s))
(defn counts-locations [s]
  (->> s
       (re-seq counts-locations-regex)
       counts-locations-from-seq-grouped))
(s/def ::station-num (s/and pos-int? #(< % 80)))
(s/def ::available-bikes (s/and int? #(<= 0 % 50)))
(s/def ::free-slots (s/and int? #(<= 0 % 50)))
(s/def ::station-latitude (s/and float? #(< 50.0 % 60.0)))
(s/def ::station-longtitude (s/and float? #(< 15.0 % 25.0)))
(s/def ::station-location (s/tuple ::station-latitude ::station-longtitude))
(s/def ::counts-location (s/keys :req-un [::station-num ::available-bikes ::free-slots ::station-location]))
(s/fdef counts-locations :args string? :ret (s/coll-of ::counts-location))
(def addresses-regex
  #"google.maps.event.trigger.gmarkers\[(\d{1,2})\]..click.{5}<b> {0,4}Stacja nr\. (\d{1,2})\. ([^\f\t\n\r\v<>]{4,}?) {0,5}?..b> {0,8}?<.a>")
(defn address-from-grouped-match [[_ from-zero from-one address]]
  (let [from-zero (read-string from-zero)
        from-one (read-string from-one)]
    (do (assert (= (inc from-zero) from-one))
        {:station-num from-one
         :address address})))
(defn addresses-from-seq-grouped [s] (map address-from-grouped-match s))
(defn addresses [s]
  (->> s
       (re-seq addresses-regex)
       addresses-from-seq-grouped))
(s/def ::address string?)
(s/def ::address-entry (s/keys :req-un [::station-num ::address]))
(s/fdef addresses :args string? :ret (s/coll-of ::address-entry))
(s/def ::station-entry (s/and ::counts-location ::address-entry))
(defn stations-merge [counts-locations addresses]
  (sort #(compare (:station-num %1) (:station-num %2))
        (map #(dissoc % ::addresses-station-num)
             (set/join counts-locations
                       (map #(set/rename-keys % {:station-num ::addresses-station-num}) addresses)
                       {:station-num ::addresses-station-num }))))
(defn stations [s]
  (stations-merge (counts-locations s) (addresses s)))
(def ^:dynamic url "https://trm24.pl/panel-trm/maps.jsp")
