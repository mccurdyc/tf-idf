(ns tf-idf.core
  (:require [clojure.java.io :as io]
            [clojure.string :as st])
  (:gen-class))

;; currently removes accented characters like: â, ê, etc.
(def non-word-regex #"[^(? )\w]")

(defn clean-file [f]
  (st/lower-case (st/replace (slurp f) non-word-regex "")))

(defn get-terms-list [s]
  (st/split s #" "))

(defn calculate-tf [m v]
 (float (/ v (count m))))

(defn update-map [f coll]
  (reduce-kv (fn [m k v] (assoc m k (f coll v))) (empty coll) coll))

(defn calculate-tf-idf [f]
  (let [file (clean-file f)
        term-list (get-terms-list file)
        term-counts (frequencies term-list)]
        (update-map calculate-tf term-counts)))
        ;; term-frequency (update-map calculate-tf term-counts)]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Provide a directory:")
  (let [files (.listFiles (io/file (read-line)))
        all-terms (doall (map get-terms files))
        freqs (doall (map frequencies all-terms))]
    (doall (map update-map calculate-tf freqs))))
