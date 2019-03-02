(ns ubihub.core
  (:gen-class)
  (:require [clojure.java.io :as io]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [environ.core :refer [env]]
            [org.httpkit.server
             :refer
             [on-close on-receive run-server send! with-channel]]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults]]
            [ring.middleware.edn :refer [wrap-edn-params]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ubihub.backend.actions :refer [init]]))

;; Denormalization

(defn filter-category [category-uuid shopping-list]
           (filter (fn [[_ {:keys [category]}]] (= category-uuid category)) shopping-list))

(defn denormalize-items [shopping-list]
  (map (fn [[uuid {:keys [name quantity]}]]
         {:uuid uuid :name name :quantity quantity})
       shopping-list))

(defn filter-items [item-filter shopping-list]
  (filter (fn [[_ {:keys [quantity]}]] (item-filter quantity)) shopping-list))

(defn filter-nil [items] (filter identity items))

(defn denormalize-goods [shopping-list categorys]
  (->> categorys
       (map (fn [[category-uuid {:keys [name]}]]
              (let [items (->> shopping-list
                               (filter-category category-uuid)
                               (filter-items #(= % 0))
                               denormalize-items)]
                (if (> (count items) 0)
                  {:category name :goods (into (vector) items)}))))
      filter-nil))

(defn denormalize-shopping-list [shopping-list categorys]
  (->> categorys
       (map (fn [[category-uuid {:keys [name]}]]
              (let [items (->> shopping-list
                               (filter-category category-uuid)
                               (filter-items #(>= % 1))
                               denormalize-items)]
                (if (> (count items) 0)
                  {:category name :shopping-items (into (vector) items)}))))
       filter-nil))

(defn denormalize-state [state]
  (let [{:keys [categorys shopping-list]} state]
    {:shopping-list (into (vector) (denormalize-shopping-list shopping-list categorys))
     :goods (into (vector) (denormalize-goods shopping-list categorys))}))

;; Reducers

(defonce actions (atom init))

(defmulti reducer (fn [state action] (:type action)))

(defmethod reducer :add-category [state action]
  (let [{:keys [uuid name]} action]
    (update state :categorys #(assoc % uuid {:name name}))))

(defmethod reducer :add-shopping-item [state action]
  (let [{:keys [uuid name category]} action]
    (update state :shopping-list #(assoc % uuid {:name name :quantity 0 :category category}))))

(defmethod reducer :remove-shopping-item [state action]
  (let [{:keys [uuid]} action]
    (assoc-in state [:shopping-list uuid :quantity] 0)))

(defmethod reducer :increase-quantity [state action]
  (let [{:keys [uuid]} action]
    (update-in state [:shopping-list uuid :quantity] #(if (>= % 9) (identity %) (inc %)))))

(defmethod reducer :decrease-quantity [state action]
  (let [{:keys [uuid]} action]
    (update-in state [:shopping-list uuid :quantity] #(if (> % 1) (dec %) (identity %)))))

(defn compute-state [actions]
  (reduce reducer {:shopping-list {}} actions))

(defn update-state [action]
  (-> (swap! actions #(conj % (read-string action))) compute-state denormalize-state pr-str))

;; Websockets

(defonce channels (atom #{}))

(defn connect! [channel]
  (send! channel (-> @actions compute-state denormalize-state pr-str))
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
  (GET "/" [] (fn [req] {:status 200
                         :headers {"Content-Type" "text/html; charset=utf-8"}
                         :body (io/file "resources/public/index.html")}))
  (route/not-found "<h1>Page not found!!!!</h1>"))

(def app
  (-> app-routes
      (wrap-defaults api-defaults)
      (wrap-edn-params)
      (wrap-resource "public")))

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 3000))]
    (reset! server (run-server (wrap-reload #'app) {:port port}))))
