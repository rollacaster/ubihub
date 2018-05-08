(ns shopping-list.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.util.response :refer [content-type response]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.edn :refer [wrap-edn-params]]
            [ring.middleware.file :refer [wrap-file]]
            [ring.middleware.resource :refer [wrap-resource]]))

(defonce app-db (atom {:shopping-list
                       [["28dbf4c7-6d90-4ad3-90a5-8fad068d6464" {:quantity 1, :good "2dd89b67-3156-4c9e-8a44-7e4523e75199"}]
                        ["59b79451-6d22-44ec-93fc-8336bed7a46c" {:quantity 8, :good "8280b3f8-e14a-45d9-be46-09ffb44b8db9"}]]
                        :goods
                       {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana"}
                        "2dd89b67-3156-4c9e-8a44-7e4523e75199" {:name "Milk"}
                        "dbea6f63-1004-4c9c-8d28-b84c866df474" {:name "Cheese"}}}))

(defn add-item
  [goodId app-db]
  (update app-db
         :shopping-list
         #(cons (vector (str (java.util.UUID/randomUUID))
                        {:quantity 1 :good goodId}) %)))

(defn remove-item
  [id app-db]
  (update app-db :shopping-list (fn [x] (remove #(= (first %) id) x))))

(defn increase-item
  [id app-db]
  (update app-db :shopping-list
          (fn [x] (map (fn [item]
                         (if (= (first item) id)
                           (vector id (update (second item) :quantity (comp #(min 9 %) inc)))
                           item)) x))))

(defn find-item
  [id app-db]
  (first (filter #(= (first %) id) (:shopping-list app-db))))

(defn decrease-item
  [id app-db]
  (let [item (find-item id app-db)
        quantity (:quantity (second item))]
    (if (> quantity  1)
      (update app-db :shopping-list
              (fn [x] (map (fn [item]
                             (if (= (first item) id)
                               (vector id (update (second item) :quantity (comp #(min 9 %) dec)))
                               item)) x)))
      (remove-item id app-db))))

(defn send-edn [x]
  (-> x
      pr-str
      response
      (content-type "application/edn")))

(defroutes app-routes
  (GET "/shopping-list" []
       (-> @app-db send-edn))
  (POST "/shopping-list" [goodId]
        (-> (swap! app-db #(add-item goodId %)) send-edn))
  (POST "/shopping-list/:goodId/increase" [goodId]
        (-> (swap! app-db #(increase-item goodId %)) send-edn))
  (POST "/shopping-list/:goodId/decrease" [goodId]
        (->  (swap! app-db #(decrease-item goodId %)) send-edn))
  (DELETE "/shopping-list/:goodId" [goodId]
          (-> (swap! app-db #(remove-item goodId %)) send-edn))
  (route/not-found "<h1>Page not found</h1>"))

(def app
  (-> app-routes
      (wrap-defaults api-defaults)
      (wrap-edn-params)
      (wrap-resource ".")))
