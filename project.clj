(defproject cjurli "0.1.0"
  :description "Cjurli is a clojure port of kurli. It lists courses of
                the CS department of Helsinki University."
  :url ""
  :license {:name "ISC license"
            :url "http://www.isc.org/software/license"}
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.clojure/data.json "0.1.2"]
                 [org.clojure/java.jdbc "0.1.2"]
                 [org.xerial/sqlite-jdbc "3.7.2"]
                 [com.oracle/ojdbc14 "10.2.0.3.0"]
                 [ring/ring-core "1.0.2"]
                 [ring/ring-jetty-adapter "1.0.2"]]
  :plugins [[lein-ring "0.6.1"]]
  :ring {:handler cjurli.core/cjurli}
  :main cjurli.core)