(ns cjurli.core
  (:require [clojure.data.json :as json]
            [clojure.java.jdbc :as jdbc]
            [ring.util.response :as r]
            [ring.adapter.jetty :as jetty]))

(def db
  (atom
   {:classname "oracle.jdbc.OracleDriver"
    :subprotocol "oracle:thin"
    :subname "@bodbacka.cs.helsinki.fi:1521:test"
    :user "USER"
    :password "PASSWORD"}))

(def sql-opintojaksot
  "SELECT * FROM opintojakso WHERE tyyppi = 'K' AND opintopisteet != 0")

(defn opintojakso->foo
  [opintojakso]
  (let [code (:kurssikoodi opintojakso)
        name {:fi (:nimi_suomi opintojakso)
              :en (:nimi_englanti opintojakso)
              :sv (:nimi_ruotsi opintojakso)}
        credits (:opintopisteet opintojakso)
        level (:taso opintojakso)]
    {:code code
     :name name
     :credits credits
     :level level}))

(defn with-opintojaksot
  [f]
  (jdbc/with-connection @db
    (jdbc/with-query-results results
      [sql-opintojaksot]
      (f (map opintojakso->foo results)))))

(defn cjurli [request]
  (cond (= (:request-method request) :get)
        (-> (r/response (with-opintojaksot
                          (fn [o] (json/json-str o :escape-unicode false))))
            (r/content-type "application/json"))
        (= (:request-method request) :head)
        (-> (r/response "")
            (r/content-type "application/json"))))

(defn -main
  [& args]
  (let [user (first args)
        password (second args)]
    (swap! db (fn [db]
                (-> db
                    (assoc :user user)
                    (assoc :password password))))
    (jetty/run-jetty cjurli {:port 8080})))