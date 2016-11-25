(ns tf-idf.core-test
  (:require [clojure.test :refer :all]
            [tf-idf.core :refer :all]))

(deftest test-get-basename-string
  (testing "get-basename-string function"
    (is (= (get-basename-string "doc1.txt") "doc1"))
    (is (= (get-basename-string "doc1.txt.txt") "doc1"))
    (is (= (get-basename-string "doc1.csv") "doc1"))
    (is (= (get-basename-string "doc1 (1).txt.csv") "doc1 (1)"))
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

(deftest test-sort-tf-idf
  (testing "sort-tf-idf"
    (is (= (sort-tf-idf {"a" 1, "b" 2, "c" 3}) {"c" 3, "b" 2, "a" 1}))
    (is (= (sort-tf-idf {"e" 2, "f" 12, "d" 3, "a" 1, "b" 1, "c" 2}) {"f" 12, "d" 3, "e" 2, "c" 2, "a" 1, "b" 1}))))

(deftest test-calculate-tf
  (testing "calculate-tf"
    (is (= (calculate-tf {"a" 3, "b" 3, "c" 3}) {"a" 1.0, "b" 1.0, "c" 1.0}))
    (is (= (calculate-tf {"a" 3, "b" 2, "c" 1}) {"a" (float (/ 3 3)), "b" (float (/ 2 3)), "c" (float (/ 1 3))}))
    (is (= (calculate-tf {"a" 10, "b" 4, "c" 1, "d" 23, "e" 100}) {"a" (float (/ 10 5)), "b" (float (/ 4 5)), "c" (float (/ 1 5)), "d" (float (/ 23 5)), "e" (float (/ 100 5))}))))

(deftest test-calculate-idf
  (testing "calculate-idf"
    (is (= (calculate-idf {"a" 3, "b" 3, "c" 3} 3) {"a" 0.0, "b" 0.0, "c" 0.0}))
    (is (= (calculate-idf {"a" 3, "b" 3, "c" 3} 6) {"a" (Math/log (float (/ 6 3))), "b" (Math/log (float (/ 6 3))), "c" (Math/log (float (/ 6 3)))}))
    (is (= (calculate-idf {"a" 3, "b" 2, "c" 1} 5) {"a" (Math/log (float (/ 5 3))), "b" (Math/log (float (/ 5 2))), "c" (Math/log (float (/ 5 1)))}))
    (is (= (calculate-idf {"a" 10, "b" 11, "c" 13} 6) {"a" (Math/log (float (/ 6 10))), "b" (Math/log (float (/ 6 11))), "c" (Math/log (float (/ 6 13)))}))
    (is (= (calculate-idf {"a" 10, "b" 11, "c" 13} 20) {"a" (Math/log (float (/ 20 10))), "b" (Math/log (float (/ 20 11))), "c" (Math/log (float (/ 20 13)))}))))

(deftest test-calculate-tf-idf
  (testing "calculate-tf-idf"
    (is (= (calculate-tf-idf {:file "test.txt" :terms {"a" (float (/ 3 3)), "b" (float (/ 3 3))}} {"a" (Math/log (float (/ 3 3))), "b" (Math/log (float (/ 3 3)))}) {:file "test.txt" :tf-idf {"a" 0.0, "b" 0.0}}))
    (is (= (calculate-tf-idf {:file "test.txt" :terms {"a" (float (/ 2 6)), "b" (float (/ 4 6))}} {"a" (Math/log (float (/ 6 3))), "b" (Math/log (float (/ 6 1)))}) {:file "test.txt" :tf-idf {"a" (* (float (/ 2 6)) (Math/log (float (/ 6 3)))), "b" (* (float (/ 4 6)) (Math/log (float (/ 6 1))))}}))
    (is (= (calculate-tf-idf {:file "test.txt" :terms {"a" (float (/ 12 6)), "b" (float (/ 4 6))}} {"a" (Math/log (float (/ 6 3))), "b" (Math/log (float (/ 6 1)))}) {:file "test.txt" :tf-idf {"a" (* (float (/ 12 6)) (Math/log (float (/ 6 3)))), "b" (* (float (/ 4 6)) (Math/log (float (/ 6 1))))}}))))
    (is (= (calculate-tf-idf {:file "test.txt" :terms {"a" (float (/ 12 6)), "b" (float (/ 14 6))}} {"a" (Math/log (float (/ 6 3))), "b" (Math/log (float (/ 6 1)))}) {:file "test.txt" :tf-idf {"a" (* (float (/ 12 6)) (Math/log (float (/ 6 3)))), "b" (* (float (/ 14 6)) (Math/log (float (/ 6 1))))}}))
    (is (= (calculate-tf-idf {:file "test.txt" :terms {"a" (float (/ 6 6)), "b" (float (/ 0 0))}} {"a" (Math/log (float (/ 6 3))), "b" (Math/log (float (/ 6 1)))}) {:file "test.txt" :tf-idf {"a" (* (float (/ 6 6)) (Math/log (float (/ 6 3)))), "b" (* (float (/ 14 6)) (Math/log (float (/ 6 1))))}}))
    (is (= (calculate-tf-idf {:file "test.txt" :terms {"a" (float (/ 6 6)), "b" (float (/ 0 0))}} {"a" (Math/log (float (/ 6 6))), "b" (Math/log (float (/ 6 0)))}) {:file "test.txt" :tf-idf {"a" (* (float (/ 12 6)) (Math/log (float (/ 6 3)))), "b" (* (float (/ 14 6)) (Math/log (float (/ 6 1))))}}))
