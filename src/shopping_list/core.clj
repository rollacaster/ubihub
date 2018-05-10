(ns shopping-list.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.util.response :refer [content-type response]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.edn :refer [wrap-edn-params]]
            [ring.middleware.file :refer [wrap-file]]
            [ring.middleware.resource :refer [wrap-resource]]))

(defonce actions (atom [{:type :add-good :uuid "2dd89b67-3156-4c9e-8a44-7e4523e75199" :name "Milk"}
                        {:type :add-good :uuid "dbea6f63-1004-4c9c-8d28-b84c866df474" :name "Cheese"}
                        {:type :add-good :uuid "8280b3f8-e14a-45d9-be46-09ffb44b8db9":name "Banana"}]))

(defmulti reducer (fn [state action] (:type action)))

(defmethod reducer :add-good [state action]
  (let [{:keys [uuid name]} action]
    (update state :goods #(assoc % uuid {:name name}))))

(defmethod reducer :add-shopping-item [state action]
  (let [{:keys [uuid good]} action]
    (update state :shopping-list #(cons (vector (str uuid) {:quantity 1 :good good}) %))))

(defmethod reducer :remove-shopping-item [state action]
  (let [{:keys [uuid]} action]
    (update state :shopping-list (fn [x] (remove #(= (first %) uuid) x)))))

(defmethod reducer :increase-quantity [state action]
  (let [{:keys [uuid]} action]
    (update state :shopping-list
            (fn [x] (map (fn [item] (if (= (first item) uuid)
                                      (vector uuid (update (second item)
                                                           :quantity (comp #(min 9 %) inc)))
                                      item)) x)))))

(defmethod reducer :decrease-quantity [state action]
  (let [{:keys [uuid]} action
        item (first (filter #(= (first %) uuid) (:shopping-list state)))
        quantity (:quantity (second item))]
    (if (> quantity  1)
      (update state :shopping-list
              (fn [x] (map (fn [item]
                             (if (= (first item) uuid)
                               (vector uuid (update (second item) :quantity (comp #(min 9 %) dec)))
                               item)) x)))
      (reducer state {:type :remove-shopping-item :uuid uuid}))))

(defn compute-state
  [actions]
  (reduce reducer {:shopping-list [] :goods {}} actions))

(defn send-edn [x]
  (-> x pr-str response (content-type "application/edn")))

(defn update-state
  [action]
  (-> (swap! actions #(conj % action)) compute-state send-edn))

(defroutes app-routes
  (GET "/shopping-list" []
       (-> @actions compute-state send-edn))
  (POST "/shopping-list" [goodId]
        (update-state {:type :add-shopping-item :uuid (java.util.UUID/randomUUID) :good goodId}))
  (POST "/shopping-list/:goodId/increase" [goodId]
        (update-state {:type :increase-quantity :uuid goodId}))
  (POST "/shopping-list/:goodId/decrease" [goodId]
        (update-state {:type :decrease-quantity :uuid goodId}))
  (DELETE "/shopping-list/:goodId" [goodId]
          (update-state {:type :remove-shopping-item :uuid goodId}))
  (route/not-found "<h1>Page not found</h1>"))

(def app
  (-> app-routes
      (wrap-defaults api-defaults)
      (wrap-edn-params)
      (wrap-resource ".")))
