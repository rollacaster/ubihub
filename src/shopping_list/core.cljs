(ns shopping-list.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<! chan]]
            [cljs.reader :refer [read-string]]))

(enable-console-print!)
;; -------------------------
;; Data

(def app-db (atom {}))
(defonce add-goods-modal-shown? (atom false))

;; -------------------------
;; Queries
(defn find-shopping-item
  [shopping-list id]
  (second (first (filter #(= (first %) id) shopping-list))))

(defn get-shopping-item
  [id app-db]
  (let [{:keys [shopping-list goods]} app-db
        listItem (find-shopping-item shopping-list id)
        good (get goods (:good listItem))]
    (merge {:id id} (assoc listItem :good (:name good)))))

(defn find-good
  [goodId app-db]
  (first (filter #(= (:good (second %)) goodId) (:shopping-list app-db))))

(defn filter-goods
  [app-db]
  (update app-db :goods (fn [goods] (remove (fn [good] (find-good (first good) app-db)) goods))))

;; -------------------------
;; Actions

(defonce ws-chan (atom nil))
(defn make-websocket! [url]
 (if-let [chan (js/WebSocket. url)]
   (do
     (set! (.-onmessage chan) #(reset! app-db (->> % .-data read-string)))
     (reset! ws-chan chan))
;; TODO Handle ws error
   (throw (js/Error. "Websocket connection failed!"))))

(defn send!
 [msg]
 (if @ws-chan
   (.send @ws-chan msg)
   (throw (js/Error. "Websocket is not available!"))))

(defn add-shopping-item
  [goodId]
  (send! {:type :add-shopping-item :uuid (str (random-uuid)) :good goodId}))

(defn remove-shopping-item
  [goodId]
  (send! {:type :remove-shopping-item :uuid goodId}))

(defn increase-quantity
  [goodId]
  (send! {:type :increase-quantity :uuid goodId}))

(defn decrease-quantity
  [goodId]
  (send! {:type :decrease-quantity :uuid goodId}))

(defn toogle-goods-modal
  []
  (let [body (.-body js/document)
        overflow (.-overflow (.-style body))]
    (if (= overflow "hidden")
      (set! (.-overflow (.-style body)) "")
      (set! (.-overflow (.-style body)) "hidden")))
  (swap! add-goods-modal-shown? not))

;; -------------------------
;; Views
(defn quantity-button
  [label on-click]
  [:button
    {:class "f4 dim ph3 pv2 mv2 dib ba b--mid-gray bg-white mid-gray a"
     :on-click on-click}
    label])

(defn quantity-counter
  [id quantity]
  [:div {:class "flex flex-column items-center"}
   (quantity-button "+" #(increase-quantity id))
   [:span {:class "f3 db black-70"} quantity]
   (quantity-button "-" #(decrease-quantity id))])

(defn shopping-item
  [item]
  [:li {:key (:id item) :class "flex items-center lh-copy pa3 bb b--black-10"}
   [:input {:id (:id item)
            :type "checkbox"
            :checked false
            :on-change #(remove-shopping-item (:id item))}]
   [:label {:for (:id item) :class "f3 db black-70 pl3 flex-auto"} (:good item)]
   (quantity-counter (:id item) (:quantity item))])

(defn add-goods-modal
  [goods]
  [:div
   {:class (str "w-100 vh-100 bg-white absolute flex-column justify-between ")
    :style {:transform (str "translateX(" (if @add-goods-modal-shown? 0 375) "px)")
            :transition "transform 0.5s"
            :top (.-scrollY js/window)}}
   [:div {:class "center pa3"}
    [:ul {:class "list ph3 pv4"}
     (map (fn [good] [:li {:class "dib mr1 mb2"
                           :key (first good)}
                      [:button {:class "f4 b db pa2 dim dark-gray ba b--black-20 bg-white"
                                :on-click #(do (add-shopping-item (first good)) (toogle-goods-modal))} (-> good second :name)]])
          goods)]]
   [:div {:class "flex justify-center"}
    [:button {:class "f5 pa2 mb2 ba white b--black-20 bg-black"
              :on-click toogle-goods-modal} "back"]]])

(defn add-button
  [goods]
  (when (> (count goods) 0)
    [:div {:class "fixed bottom-0 right-0 ma3"}
     [:button {:class "f2 br-100 h3 w3 mb2 white bg-mid-gray shadow-5"
               :on-click toogle-goods-modal} "+"]]))

(defn main []
  (let [app-db @app-db
        add-goods-modal-shown? @add-goods-modal-shown?
        goods (:goods (filter-goods app-db))]
    [:div {:class (when add-goods-modal-shown? "m0 h-100 overflow-hidden")}
     [:div {:class "relative sans-serif mw5 center pa3"}
      [:ul {:class "list pl0 mt0 measure center"}
       (map (comp shopping-item #(get-shopping-item (first %) app-db)) (:shopping-list app-db))]]
     (add-button goods)
     (add-goods-modal goods)]))


;; -------------------------
;; Initialize app
(do
  (reagent/render [main] (.getElementById js/document "app"))
  (make-websocket! "ws://192.168.0.229:3000/ws"))
