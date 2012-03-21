(ns cjurli.mock
  (:require [clojure.java.jdbc :as jdbc]))

(def db
  {:classname "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname "test.db"})

(defn create-opintojakso
  []
  (jdbc/create-table
   :opintojakso
   [:kurssikoodi "PRIMARY KEY"]
   [:nimi_suomi "TEXT"]
   [:nimi_englanti "TEXT"]
   [:nimi_ruotsi "TEXT"]
   [:opintopisteet "INTEGER"]
   [:taso "TEXT"]
   [:tyyppi "TEXT"]))

(defn drop-opintojakso
  []
  (try
    (jdbc/drop-table :opintojakso)
    (catch Exception _)))

(defn init
  []
  (jdbc/with-connection db
    (drop-opintojakso)
    (create-opintojakso)
    (jdbc/insert-record
     :opintojakso
     {:kurssikoodi 581544
      :nimi_suomi "Olio-ohjelmointi"
      :nimi_englanti "Object-Oriented Programming"
      :nimi_ruotsi "Objektprogrammering"
      :opintopisteet 8
      :taso "L"
      :tyyppi "K"})))

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

(defn opintojaksot
  []
  (jdbc/with-connection db
    (jdbc/with-query-results results
      [sql-opintojaksot]
      (map opintojakso->foo results))))
