(ns tf-idf.core-test
  (:require [clojure.test :refer :all]
            [tf-idf.core :refer :all]))

;; admittedly, I should have used functions to automatically generate test data.

(deftest test-get-basename-string
  (testing "get-basename-string function"
    (is (= (get-basename-string "doc1.txt") "doc1"))
    (is (= (get-basename-string "doc1.txt.txt") "doc1.txt"))
    (is (= (get-basename-string "doc1.csv") "doc1"))
    (is (= (get-basename-string "doc1 (1).txt.csv") "doc1 (1).txt"))
    (is (= (get-basename-string "DoC1.txt") "DoC1"))
    (is (= (get-basename-string "DoCÀ1.txt") "DoCÀ1"))))

(deftest test-clean-file
  (testing "clean-file function"
    (is (= (clean-file "test-data/test-doc1.txt") "hello there how are you   today "))
    (is (= (clean-file "test-data/test-doc2.txt") "h  e  l  l  o "))
    (is (= (clean-file "test-data/test-doc3.txt") "c     o l to    n "))
    (is (= (clean-file "test-data/test-doc4.txt") "çolton "))
    (is (= (clean-file "test-data/test-doc5.txt") "my  name      again  cçolto  n "))
    (is (= (clean-file "test-data/test-doc6.txt") "hello there bobby mccurdy adasd ç ç çç hello bçiadf colton bobby bobby this is asdf a asdft2132 test0 "))
    (is (= (clean-file "test-data/test-doc7.txt") "colton asf atest  ç çç çç hello bçiadf coççlton  colton colton  more coltons coltons   newline gotta catch them all  another "))))

(deftest test-get-terms-list
  (testing "get-terms-list function"
    (is (= (get-terms-list "hello there colton how are you today ") ["hello" "there" "colton" "how" "are" "you" "today"]))
    (is (= (get-terms-list "hello  there              colton") ["hello" "there" "colton"]))
    (is (= (get-terms-list "             hello  there              colton             ") ["hello" "there" "colton"]))))

(deftest test-get-in-terms
  (testing "get-in-terms"
    (is (= (get-in-terms {:file "test.txt" :terms {"a" 1, "b" 2, "c" 3}}) {"a" 1, "b" 2, "c" 3}))))

(deftest test-per-term-doc-count
  (testing "per-term-doc-count"
    (is (= (per-term-doc-count '({:file "test.txt" :terms {"a" 1, "b" 2, "c" 3}},
                                 {:file "test1.txt" :terms {"a" 2, "c" 6, "d" 3}})) {"a" 2, "b" 1, "c" 2, "d" 1}))
    (is (= (per-term-doc-count '({:file "test.txt" :terms {"a" 1, "b" 2, "c" 3}},
                                 {:file "test1.txt" :terms {"e" 13, "c" 11, "f" 3, "a" 2}},
                                 {:file "test2.txt" :terms {"a" 2, "c" 6, "d" 3}})) {"a" 3, "b" 1, "c" 3, "d" 1, "e" 1, "f" 1}))))

(deftest test-calculate-tf
  (testing "calculate-tf"
    (is (= (calculate-tf {"a" 3, "b" 3, "c" 3} 9) {"a" (float (/ 3 9)), "b" (float (/ 3 9)), "c" (float (/ 3 9))}))
    (is (= (calculate-tf {"a" 3, "b" 2, "c" 1} 6) {"a" (float (/ 3 6)), "b" (float (/ 2 6)), "c" (float (/ 1 6))}))
    (is (= (calculate-tf {"a" 10, "b" 4, "c" 1, "d" 23, "e" 100} 138) {"a" (float (/ 10 138)), "b" (float (/ 4 138)), "c" (float (/ 1 138)), "d" (float (/ 23 138)), "e" (float (/ 100 138))}))))

(deftest test-calculate-idf
  (testing "calculate-idf"
    (is (= (calculate-idf {"a" 3, "b" 3, "c" 3} 3) {"a" 0.0, "b" 0.0, "c" 0.0}))
    (is (= (calculate-idf {"a" 3, "b" 3, "c" 3} 6) {"a" (Math/log10 (float (/ 6 3))), "b" (Math/log10 (float (/ 6 3))), "c" (Math/log10 (float (/ 6 3)))}))
    (is (= (calculate-idf {"a" 3, "b" 2, "c" 1} 5) {"a" (Math/log10 (float (/ 5 3))), "b" (Math/log10 (float (/ 5 2))), "c" (Math/log10 (float (/ 5 1)))}))
    (is (= (calculate-idf {"a" 10, "b" 11, "c" 13} 6) {"a" (Math/log10 (float (/ 6 10))), "b" (Math/log10 (float (/ 6 11))), "c" (Math/log10 (float (/ 6 13)))}))
    (is (= (calculate-idf {"a" 10, "b" 11, "c" 13} 20) {"a" (Math/log10 (float (/ 20 10))), "b" (Math/log10 (float (/ 20 11))), "c" (Math/log10 (float (/ 20 13)))}))))

(deftest test-calculate-tf-idf
  (testing "calculate-tf-idf"
    (is (= (calculate-tf-idf {:file "test.txt" :terms {"a" (float (/ 3 3))}} {"a" (Math/log10 (float (/ 3 3)))}) {:file "test.txt" :tf-idf '(["a" 0.0])}))
    (is (= (calculate-tf-idf {:file "test.txt" :terms {"a" (float (/ 3 3))}} {"a" (Math/log10 (float (/ 6 3)))}) {:file "test.txt" :tf-idf '(["a" 0.3010299956639812])}))
    (is (= (calculate-tf-idf {:file "test.txt" :terms {"a" (float (/ 1 3))}} {"a" (Math/log10 (float (/ 6 6)))}) {:file "test.txt" :tf-idf '(["a" 0.0])}))
    (is (= (calculate-tf-idf {:file "test.txt" :terms {"a" (float (/ 1 3))}} {"a" (Math/log10 (float (/ 6 3)))}) {:file "test.txt" :tf-idf '(["a" 0.10034333487845806])}))
    (is (= (calculate-tf-idf {:file "test.txt" :terms {"a" (float (/ 2 3))}} {"a" (Math/log10 (float (/ 6 4)))}) {:file "test.txt" :tf-idf '(["a"0.11739417620240647])}))
    (is (= (calculate-tf-idf {:file "test.txt" :terms {"a" (float (/ 2 4)), "b" (float (/ 2 4))}} {"a" (Math/log10 (float (/ 6 6))), "b" (Math/log10 (float (/ 6 6)))})
                             {:file "test.txt" :tf-idf '(["a" 0.0], ["b" 0.0])}))
    (is (= (calculate-tf-idf {:file "test.txt" :terms {"a" (float (/ 2 4)), "b" (float (/ 2 4))}} {"a" (Math/log10 (float (/ 6 3))), "b" (Math/log10 (float (/ 6 3)))})
                             {:file "test.txt" :tf-idf '(["a" 0.1505149978319906], ["b" 0.1505149978319906])}))
    (is (= (calculate-tf-idf {:file "test.txt" :terms {"a" (float (/ 2 4)), "b" (float (/ 2 4))}} {"a" (Math/log10 (float (/ 6 6))), "b" (Math/log10 (float (/ 6 3)))})
                             {:file "test.txt" :tf-idf '(["b" 0.1505149978319906], ["a" 0.0])}))
    (is (= (calculate-tf-idf {:file "test.txt" :terms {"a" (float (/ 2 4)), "b" (float (/ 2 4))}} {"a" (Math/log10 (float (/ 6 1))), "b" (Math/log10 (float (/ 6 4)))})
                             {:file "test.txt" :tf-idf '(["a" 0.3890756251918218], ["b" 0.08804562952784062])}))
    (is (= (calculate-tf-idf {:file "test.txt" :terms {"a" (float (/ 3 4)), "b" (float (/ 1 4))}} {"a" (Math/log10 (float (/ 6 6))), "b" (Math/log10 (float (/ 6 6)))})
                             {:file "test.txt" :tf-idf '(["a" 0.0] , ["b" 0.0])}))
    (is (= (calculate-tf-idf {:file "test.txt" :terms {"a" (float (/ 3 4)), "b" (float (/ 1 4))}} {"a" (Math/log10 (float (/ 6 3))), "b" (Math/log10 (float (/ 6 3)))})
                             {:file "test.txt" :tf-idf '(["a" 0.22577249674798588] , ["b" 0.0752574989159953])}))
    (is (= (calculate-tf-idf {:file "test.txt" :terms {"a" (float (/ 3 4)), "b" (float (/ 1 4))}} {"a" (Math/log10 (float (/ 6 6))), "b" (Math/log10 (float (/ 6 3)))})
                             {:file "test.txt" :tf-idf '(["b" 0.0752574989159953], ["a" 0.0])}))
    (is (= (calculate-tf-idf {:file "test.txt" :terms {"a" (float (/ 3 4)), "b" (float (/ 1 4))}} {"a" (Math/log10 (float (/ 6 1))), "b" (Math/log10 (float (/ 6 4)))})
                             {:file "test.txt" :tf-idf '(["a" 0.5836134377877327], ["b" 0.04402281476392031])}))))

(deftest test-get-tf
  (testing "get-tf"
    (is (= (get-tf (clojure.java.io/file "test-data/test-doc1.txt")) {:file "test-doc1.txt" :terms (calculate-tf {"hello" 1,
                                                                                                                  "there" 1,
                                                                                                                  "how" 1,
                                                                                                                  "are" 1,
                                                                                                                  "you" 1,
                                                                                                                  "today" 1} (count (get-terms-list (clean-file "test-data/test-doc1.txt"))))}))
    (is (= (get-tf (clojure.java.io/file "test-data/test-doc2.txt")) {:file "test-doc2.txt" :terms (calculate-tf {"h" 1,
                                                                                                                  "e" 1,
                                                                                                                  "l" 2,
                                                                                                                  "o" 1} (count (get-terms-list (clean-file "test-data/test-doc2.txt"))))}))
    (is (= (get-tf (clojure.java.io/file "test-data/test-doc3.txt")) {:file "test-doc3.txt" :terms (calculate-tf {"c" 1,
                                                                                                                  "o" 1,
                                                                                                                  "l" 1,
                                                                                                                  "to" 1,
                                                                                                                  "n" 1} (count (get-terms-list (clean-file "test-data/test-doc3.txt"))))}))
    (is (= (get-tf (clojure.java.io/file "test-data/test-doc4.txt")) {:file "test-doc4.txt" :terms (calculate-tf {"çolton" 1} (count (get-terms-list (clean-file "test-data/test-doc4.txt"))))}))
    (is (= (get-tf (clojure.java.io/file "test-data/test-doc5.txt")) {:file "test-doc5.txt" :terms (calculate-tf {"my" 1,
                                                                                                                  "name" 1,
                                                                                                                  "again" 1,
                                                                                                                  "cçolto" 1,
                                                                                                                  "n" 1} (count (get-terms-list (clean-file "test-data/test-doc5.txt"))))}))
    (is (= (get-tf (clojure.java.io/file "test-data/test-doc6.txt")) {:file "test-doc6.txt" :terms (calculate-tf {"hello" 2,
                                                                                                                  "there" 1,
                                                                                                                  "bobby" 3,
                                                                                                                  "mccurdy" 1,
                                                                                                                  "adasd" 1,
                                                                                                                  "ç" 2,
                                                                                                                  "çç" 1,
                                                                                                                  "bçiadf" 1,
                                                                                                                  "colton" 1,
                                                                                                                  "this" 1,
                                                                                                                  "is" 1,
                                                                                                                  "asdf" 1,
                                                                                                                  "a" 1,
                                                                                                                  "asdft2132" 1,
                                                                                                                  "test0" 1} (count (get-terms-list (clean-file "test-data/test-doc6.txt"))))}))
    (is (= (get-tf (clojure.java.io/file "test-data/test-doc7.txt")) {:file "test-doc7.txt" :terms (calculate-tf {"colton" 3,
                                                                                                                  "asf" 1,
                                                                                                                  "atest" 1,
                                                                                                                  "ç" 1,
                                                                                                                  "çç" 2,
                                                                                                                  "hello" 1,
                                                                                                                  "bçiadf" 1,
                                                                                                                  "coççlton" 1,
                                                                                                                  "more" 1,
                                                                                                                  "coltons" 2,
                                                                                                                  "newline" 1,
                                                                                                                  "gotta" 1,
                                                                                                                  "catch" 1,
                                                                                                                  "them" 1,
                                                                                                                  "all" 1,
                                                                                                                  "another" 1} (count (get-terms-list (clean-file "test-data/test-doc7.txt"))))}))
    (is (= (get-tf (clojure.java.io/file "test-data/test-doc8.txt")) {:file "test-doc8.txt" :terms (calculate-tf {"colton" 5} (count (get-terms-list (clean-file "test-data/test-doc8.txt"))))}))
    (is (= (get-tf (clojure.java.io/file "test-data/test-doc9.txt")) {:file "test-doc9.txt" :terms (calculate-tf {"colton" 4,
                                                                                                                  "mccurdy" 1} (count (get-terms-list (clean-file "test-data/test-doc9.txt"))))}))
    (is (= (get-tf (clojure.java.io/file "test-data/test-doc10.txt")) {:file "test-doc10.txt" :terms (calculate-tf {} (count (get-terms-list (clean-file "test-data/test-doc10.txt"))))}))))

(deftest test-get-idf
  (testing "get-idf"
    (let [group1 (get-tf (clojure.java.io/file "test-data/test-doc1.txt"))
          group2 (doall (map get-tf [(clojure.java.io/file "test-data/test-doc1.txt"), (clojure.java.io/file "test-data/test-doc2.txt")]))
          group3 (doall (map get-tf [(clojure.java.io/file "test-data/test-doc1.txt"), (clojure.java.io/file "test-data/test-doc2.txt"), (clojure.java.io/file "test-data/test-doc3.txt")]))]
    (is (= (get-idf group1) {nil 0.0})) ;; this should be changed to throw error or something else
    (is (= (get-idf group2) (calculate-idf (per-term-doc-count group2) 2)))
    (is (= (get-idf group3) (calculate-idf (per-term-doc-count group3) 3))))))

(deftest test-get-tf-idf
  (testing "get-tf-idf"
    (is (= (get-tf-idf '({:file "test1.txt" :terms {"a" 0.5}} {:file "test2.txt" :terms {"a" 0.2}}) {"a" 0.0})
          '({:file "test2.txt" :tf-idf (["a" 0.0])} {:file "test1.txt" :tf-idf (["a" 0.0])})))
    (is (= (get-tf-idf '({:file "test1.txt" :terms {"a" 0.5}} {:file "test2.txt" :terms {"b" 0.9}}) {"a" 2, "b" 2})
          '({:file "test2.txt" :tf-idf (["b" 1.8])} {:file "test1.txt" :tf-idf (["a" 1.0])})))
    (is (= (get-tf-idf '({:file "test1.txt" :terms {"a" 0.5, "b" 0.5}} {:file "test2.txt" :terms {"a" 0.2, "c" 0.8}}) {"a" 0.0, "b" 2.0, "c" 2.0})
          '({:file "test2.txt" :tf-idf (["c" 1.6], ["a" 0.0])} {:file "test1.txt" :tf-idf (["b" 1.0], ["a" 0.0])})))))
