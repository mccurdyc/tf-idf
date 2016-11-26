(defproject tf-idf "0.1.0-SNAPSHOT"
  :description "A term-frequency inverse-document-frequency calculator"
  :url "https://github.com/mccurdyc/tf-idf"
  :license {:name "GNU General Public License v3.0"
            :url "https://www.gnu.org/licenses/gpl-3.0.en.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-time "0.12.2"]
                 [org.clojure/tools.cli "0.3.5"]]
  :main ^:skip-aot tf-idf.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
