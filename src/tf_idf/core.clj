(ns tf-idf.core
  (:require [clojure.java.io :as io]
            [clojure.string :as st])
  (:gen-class))

;; currently removes accented characters like: â, ê, etc.
(def non-word-regex #"[^(? )\w]")

(defn get-terms [f]
  "Replace any non-word or non-whitespace characters with ''. Everything to lower-case. Split by ' ' character."
    (st/split (st/lower-case (st/replace (slurp f) non-word-regex "")) #" "))

(defn term-frequency [l]
  (reduce (quot (frequencies l) (count l))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Provide a directory:")
  (let [files (file-seq (io/file (read-line)))
        all-terms (doall (map get-terms (str files)))]))
