(ns shopping-list.core
  (:require [compojure.core :refer :all]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [org.httpkit.server :refer [send! run-server with-channel on-close on-receive]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.edn :refer [wrap-edn-params]]
            [ring.middleware.file :refer [wrap-file]]
            [ring.middleware.reload :refer [wrap-reload]]
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

(defn update-state
  [action]
  (-> (swap! actions #(conj % (read-string action))) compute-state pr-str))

(defonce channels (atom #{}))

(defn connect! [channel]
  (send! channel (-> @actions compute-state pr-str))
  (swap! channels conj channel))

(defn disconnect! [channel status]
  (println "channel closed:" status)
  (swap! channels #(remove #{channel} %)))

(defn notify-clients [msg]
  (let [state (update-state msg)]
    (doseq [channel @channels]
      (send! channel state))))

(defn ws-handler [request]
  (with-channel request channel
    (connect! channel)
    (on-close channel (partial disconnect! channel))
    (on-receive channel #(notify-clients %))))

(defroutes app-routes
  (GET "/ws" [] ws-handler)
  (route/not-found "<h1>Page not found!!!!</h1>"))

(def app
  (-> app-routes
      (wrap-defaults api-defaults)
      (wrap-edn-params)
      (wrap-resource ".")))

(defn -main [& args]
  (run-server (wrap-reload #'app) {:port 3000}))
