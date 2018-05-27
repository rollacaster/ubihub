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
  (js/scrollTo 0 0)
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
  [{:keys [uuid name quantity]}]
  [:li {:key uuid :class "flex items-center lh-copy pa3 bb b--black-10"}
   [:input {:id uuid
            :type "checkbox"
            :checked false
            :on-change #(remove-shopping-item uuid)}]
   [:label {:for uuid :class "f3 db black-70 pl3 flex-auto"} name]
   (quantity-counter uuid quantity)])

(defn good [{:keys [uuid name]}]
  [:li {:class "dib mr1 mb2" :key uuid}
   [:button {:class "f5 b db pa2 dim dark-gray ba b--black-20 bg-white"
             :on-click #(do (increase-quantity uuid) (toogle-goods-modal))} name]])

(defn add-goods-modal
  [goods]
  [:div
   {:class (str "w-100 vh-100 bg-white absolute flex-column justify-between ")
    :style {:transform (str "translateX(" (if @add-goods-modal-shown? 0 375) "px)")
            :transition "transform 0.5s"}}
   [:div {:class "center pa3"}
    (map (fn [{:keys [category goods]}]
           [:div {:key category}
            [:h2 {:class "f6"} category]
            [:ul (map good goods)]])
         goods)]
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
        {:keys [goods shopping-list]} app-db]
    [:div
     [:div {:class (str "relative sans-serif mw5 center pa3 overflow-x-hidden"
                        (if add-goods-modal-shown? " dn" " "))}
      [:ul {:class "list pl0 mt0 measure center"}
       (map (fn [{:keys [category shopping-items]}]
              [:div {:key category}
               [:h2 {:class "f6"} category]
               (map shopping-item shopping-items)])
            shopping-list)]]
     (when (not add-goods-modal-shown?) (add-button goods))
     (add-goods-modal goods)]))


;; -------------------------
;; Initialize app
(do
  (reagent/render [main] (.getElementById js/document "app"))
  (make-websocket! "ws://192.168.0.229:3000/ws"))
