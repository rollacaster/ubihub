(ns shopping-list.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<! chan]]
            [cljs.reader :refer [read-string]]))

(enable-console-print!)
;; -------------------------
;; Colors
(def primary "dark-blue")
(def secondary "white")
(def border "b--black-10")
(def font "black-70")
(def font-secondary "white")
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
   {:class (str "f4 dim ph3 pv2 mv2 dib ba " border)
     :on-click on-click}
    label])

(defn quantity-counter
  [id quantity]
  [:span {:class (str "f4 db " font " ba " border " ph3 br3")} quantity])

(defn shopping-item
  [{:keys [uuid name quantity]}]
  [:li {:key uuid :class (str "flex items-center lh-copy pv3 bb " border)}
   (quantity-counter uuid quantity)
   [:label {:for uuid :class (str "f4 db " font " pl2 flex-auto")} name]
   [:div {:class (str "w2 h2 bg-" primary " " font-secondary " br2 shadow-5")}
    [:ion-icon {:name "checkmark" :class "f3" :style {:transform "translateX(4px)"}
                :on-click #(remove-shopping-item uuid)}]]])

(defn good [{:keys [uuid name]}]
  [:li {:class (str "dib mr1 mb2 " font-secondary) :key uuid}
   [:button {:class (str "f5 b db pa2 dim ba b--black-20 bg-" primary " " font-secondary)
             :on-click #(do (increase-quantity uuid) (toogle-goods-modal))} name]])

(defn add-goods-modal
  [goods]
  [:div
   {:class (str "w-100 vh-100 bg-" secondary " absolute flex-column justify-between ")
    :style {:transform (str "translateX(" (if @add-goods-modal-shown? 0 375) "px)")
            :transition "transform 0.5s"}}
   [:div {:class "center pa3"}
    (map (fn [{:keys [category goods]}]
           [:div {:key category}
            [:h2 {:class (str "f5 " font)} category]
            [:ul (map good goods)]])
         goods)]
   [:div {:class "flex justify-center"}
    [:button {:class (str "f5 pa2 mb2 ba white bg-" primary)
              :on-click toogle-goods-modal} "back"]]])

(defn add-button
  [goods]
  (when (> (count goods) 0)
    [:div {:class "fixed bottom-0 right-0 ma3"}
     [:button {:class (str "f2 br-100 h3 w3 mb2 " font-secondary " bg-" primary " shadow-5")
               :on-click toogle-goods-modal} "+"]]))

(defn main []
  (let [app-db @app-db
        add-goods-modal-shown? @add-goods-modal-shown?
        {:keys [goods shopping-list]} app-db]
    [:div
     [:div {:class (str "relative sans-serif center overflow-x-hidden"
                        (if add-goods-modal-shown? " dn" " "))}
      [:header {:class (str "fixed w-100 bg-" primary " " font-secondary "  pa3 z-5")}
       [:div {:class "mb3"}
        [:span {:class "f2"} "UbiHub"]]
       [:div {:class "flex w100 f4 justify-around"}
        [:span {:class "bb b--white pb1"} "SHOPPING"]
        [:span "GOODS"]]]
      [:div {:class "ph3 mt6"}
       [:ul {:class "list pl0 mt0 measure center"}
        (map (fn [{:keys [category shopping-items]}]
               (map shopping-item shopping-items))
             shopping-list)]]]
     (when (not add-goods-modal-shown?) (add-button goods))
     (add-goods-modal goods)]))


;; -------------------------
;; Initialize app
(do
  (reagent/render [main] (.getElementById js/document "app"))
  (make-websocket! (str "ws://"(-> js/document .-location .-hostname) ":"
                        (-> js/document .-location .-port) "/ws")))
