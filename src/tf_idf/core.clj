(ns tf-idf.core
  (:require [clojure.java.io :as io]
            [clojure.string :as st])
  (:gen-class))

;; currently removes accented characters like: â, ê, etc.
(def non-word-regex #"[^(? )\w]")

(defn get-basename-string [s]
  "get basename of (string) file"
  (re-find (re-pattern ".*[^.txt]") s))

(defn clean-file [f]
  (-> (slurp f)
      (st/replace non-word-regex "")
      st/lower-case))

(defn get-terms-list [s]
  (st/split s #" "))

(defn get-in-terms [m]
  (get-in m [:terms]))

(defn per-term-doc-count [m]
  "make list of all keys from nested maps"
  (frequencies (flatten (conj '() (map keys (map get-in-terms m))))))

(defn calculate-tf [m]
  "divide occurrences of a term by the total number of terms in a single document"
  (reduce-kv (fn [n k v] (assoc n k (float (/ v (count m))))) (empty m) m))

(defn calculate-idf [m c]
  "divide total number of documents by number of documents with term. then, take the log_e"
  (reduce-kv (fn [n k v] (assoc n k (Math/log (float (/ c v))))) (empty m) m))

(defn calculate-tf-idf [tf idf]
  "calculate tf-idf (tf * idf) for a term"
  (let [file-name (get tf :file)
        tf-idf (reduce-kv (fn [n k v] (assoc n k (* v (get idf k)))) (empty tf) (get-in-terms tf))]
    {:file file-name
     :tf-idf tf-idf}))

(defn get-tf [f]
  "remove punctuation from file. get all terms from file. get occurrences of each term. calculate term frequency."
  (let [file-name (.getName f)
        file (clean-file f)
        term-list (get-terms-list file)
        term-counts (frequencies term-list)]
    {:file file-name
     :terms (calculate-tf term-counts)}))

(defn get-idf [m]
  "count number of documents term occurs in then calculate inverse document frequency for terms"
  (-> (per-term-doc-count m)
    (calculate-idf (count m))))

(defn get-tf-idf [m n]
  "loop through all term-frequency maps and calculate tf-idf for each one"
  (loop [tf-idf-map (empty '())
         l (first m)
         r (rest m)]
    (if l
      (recur (conj tf-idf-map (calculate-tf-idf l n))
             (first r)
             (rest r))
      tf-idf-map)))

(defn output-to-file [m]
  (let [base-file-name (get-basename-string (get m :file))
        output-file (io/file (str (System/getProperty "user.dir") "/output/" base-file-name "-output.txt"))]
    ;; (if (.exists output-file)
      ;; (spit output-file (get m :tf-idf))
      (.mkdir (io/file (.getParent output-file)))
      (spit output-file (get m :tf-idf))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Provide a directory:")
  (let [files (.listFiles (io/file (read-line)))
        term-tf (doall (map get-tf files))
        all-terms (per-term-doc-count term-tf)
        term-idf (get-idf term-tf)
        term-tf-idf (get-tf-idf term-tf term-idf)]
    (map output-to-file term-tf-idf)))
