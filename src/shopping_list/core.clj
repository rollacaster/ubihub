(ns shopping-list.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.edn :refer [wrap-edn-params]]
            [ring.middleware.file :refer [wrap-file]]
            [ring.middleware.resource :refer [wrap-resource]]))

(def app-db (atom '{:shopping-list {"59b79451-6d22-44ec-93fc-8336bed7a46c" {:quantity 8 :good "8280b3f8-e14a-45d9-be46-09ffb44b8db9"}
                                        "28dbf4c7-6d90-4ad3-90a5-8fad068d6464" {:quantity 2 :good "2dd89b67-3156-4c9e-8a44-7e4523e75199"}}
                        :goods {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana"}
                                "2dd89b67-3156-4c9e-8a44-7e4523e75199" {:name "Milk"}
                                "dbea6f63-1004-4c9c-8d28-b84c866df474" {:name "Cheese"}}}))

(defn shopping-list [request]
  {:status 200
   :headers {"Content-Type" "application/edn"}
   :body (pr-str @app-db)})

(defn add-item
  [goodId]
  (swap! app-db #(assoc % :shopping-list (conj (:shopping-list %) (hash-map (str (java.util.UUID/randomUUID)) {:quantity 1 :good goodId}))))
  {:status 200
   :headers {"Content-Type" "application/edn"}
   :body (pr-str @app-db)})

(defn remove-shopping-item
  [app-db id]
  (merge app-db {:shopping-list (dissoc (:shopping-list app-db) id)}))

(defn remove-item
  [goodId]
  (swap! app-db #(remove-shopping-item % goodId))
  {:status 200
   :headers {"Content-Type" "application/edn"}
   :body (pr-str @app-db)})

(defn increase-item
  [id]
  (swap! app-db #(update-in % [:shopping-list id :quantity] (comp (fn [x] (min 9 x)) inc)))
  {:status 200
   :headers {"Content-Type" "application/edn"}
   :body (pr-str @app-db)})

(defn decrease-item
  [id]
  (let [quantity (get-in @app-db [:shopping-list id :quantity])]
    (swap! app-db #(if (> quantity  1)
                     (update-in % [:shopping-list id :quantity] dec)
                     (remove-shopping-item % id)))
    {:status 200
     :headers {"Content-Type" "application/edn"}
     :body (pr-str @app-db)}))

(defroutes app-routes
  (GET "/shopping-list" [] shopping-list)
  (POST "/shopping-list" [goodId] (add-item goodId))
  (POST "/shopping-list/:goodId/increase" [goodId] (increase-item goodId))
  (POST "/shopping-list/:goodId/decrease" [goodId] (decrease-item goodId))
  (DELETE "/shopping-list/:goodId" [goodId] (remove-item goodId))
  (route/not-found "<h1>Page not found</h1>"))

(def app
  (-> app-routes
      (wrap-defaults api-defaults)
      (wrap-edn-params)
      (wrap-resource ".")))
