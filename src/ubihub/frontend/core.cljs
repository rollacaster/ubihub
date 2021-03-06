(ns ubihub.frontend.core
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs.core.async :refer [<! chan] :include-macros true]
            [cljs.reader :refer [read-string]]))

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
(defonce open-counter (atom nil))
(defonce open-category (atom nil))

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

(defn send! [msg]
  (if @ws-chan
    (.send @ws-chan msg)
    (throw (js/Error. "Websocket is not available!"))))

(defn remove-shopping-item [goodId]
  (send! {:type :remove-shopping-item :uuid goodId}))

(defn increase-quantity [goodId]
  (send! {:type :increase-quantity :uuid goodId}))

(defn decrease-quantity [goodId]
  (send! {:type :decrease-quantity :uuid goodId}))

(defn toogle-goods-modal []
  (js/scrollTo 0 0)
  (swap! add-goods-modal-shown? not))

;; -------------------------
;; Views
(defn quantity-counter [open uuid quantity]
  [:div {:className "relative"}
   [:button {:className (str "f4 db pointer outline-0 " font " ba " border " ph3 br3 bg-" secondary)
             :id "counter-button"
             :on-click #(reset! open-counter uuid)}
    quantity]
   [:div {:className "relative z-2"}
    [:div { :className "absolute"  :id "counter"
           :style {:top -48 :left 40
                   :transform (str "scale(" (if open 1 0) ")")
                   :transform-origin "0% 50%"
                   :transition "transform 0.2s cubic-bezier(0.175, 0.885, 0.32, 1.275)"}}
     [:button {:className (str "outline-0 absolute pointer z-2 f3 bn bg-" secondary)
               :style {:left 25}
               :on-click #(increase-quantity uuid)} "+"]
     [:svg {:width 63 :height 69 :viewBox "0 0 106 138"
            :style {:filter "drop-shadow(-2px -1px 3px rgba(0,0,0,0.3))"}}
      [:path {:fill secondary
              :d "M27,89 L0,69 L27,49 L27,8 C27,3.581722 30.581722,8.11624501e-16 35,0 L98,0 C102.418278,-8.11624501e-16 106,3.581722 106,8 L106,130 C106,134.418278 102.418278,138 98,138 L35,138 C30.581722,138 27,134.418278 27,130 L27,89 Z"}]]
     [:button {:className (str "outline-0 absolute pointer z-2 f3 bn bg-" secondary)
               :style {:top 39 :left 29}
               :on-click #(decrease-quantity uuid)} "-"]]]])

(defn shopping-item
  [open-counter {:keys [uuid name quantity]}]
  [:li {:key uuid :class (str "flex items-center lh-copy pv3 bb " border)}
   (quantity-counter (= uuid open-counter) uuid quantity )
   [:label {:for uuid :class (str "f4 db " font " pl2 flex-auto")} name]
   [:div {:class (str "w2 h2 bg-" primary " " font-secondary " br2 shadow-5")}
    [:ion-icon {:name "checkmark" :class "w3 h3" :on-click #(remove-shopping-item uuid)}]]])

(defn good [{:keys [uuid name]}]
  [:li {:className (str "dib mr1 mb2 " font-secondary) :key uuid}
   [:button {:className (str "f6 br-pill b db pa2 dim ba b--black-20 bg-" primary " " font-secondary)
             :on-click #(increase-quantity uuid)} name]])

(defn add-goods-modal [goods open]
  [:div
   {:className (str "w-100 vh-100 bg-" secondary " absolute flex-column justify-between ")
    :style {:transform (str "translateX(" (if @add-goods-modal-shown? 0 375) "px)")
            :transition "transform 0.5s"}}
   [:div
    (map (fn [{:keys [category goods]}]
           [:div {:className " bt bg-" :key category}
            [:button { :className (str "flex bg-" secondary " bn justify-between w-100 w-100 f4 ph3 pv3 " font)
                      :on-click #(swap! open-category (fn [open-category] (if (= open-category category) nil category)))}
             [:span category]
             [:ion-icon {:name "add" :className (str "f3 " primary)
                         :style {:transform "translateX(4px)"}}]]
            (when (= open category) [:ul (map good goods)])])
         goods)]])

(defn add-button [goods]
  (when (> (count goods) 0)
    [:div {:className "fixed bottom-0 right-0 ma3"}
     [:button {:className (str "f2 br-100 h3 w3 mb2 " font-secondary " bg-" primary " shadow-5")
               :on-click toogle-goods-modal} "+"]]))

(defn link-button [label active]
  [:a {:className (str "link " secondary " " (when active "bb b--white pb1"))
       :href "#"
       :on-click #(when-not active (toogle-goods-modal))} label])

(defn main []
  (let [app-db @app-db
        add-goods-modal-shown? @add-goods-modal-shown?
        open-count @open-counter
        {:keys [goods shopping-list]} app-db
        open-category @open-category]
    [:div {:className "sans-serif h-100"
           :on-click (fn [e]
                       (and
                        (not (= (-> e .-target .-parentElement .-id) "counter"))
                        (not (= (-> e .-target .-id) "counter-button"))
                        open-count
                        (reset! open-counter nil)))}
     [:div {:className (str "relative center overflow-x-hidden")}
      [:header {:className "mb6"}
       [:div {:className (str "fixed w-100 bg-" primary " " font-secondary "  pa3 z-1")}
        [:div {:className "mb4"}
         [:span {:className "f2"} "UbiHub"]]
        [:div {:className "flex w100 f4 justify-around"}
         (link-button "SHOPPING" (not add-goods-modal-shown?))
         (link-button "GOODS" add-goods-modal-shown?)]]]
      [:div {:className (str"ph3 " (if add-goods-modal-shown? " dn" " "))}
       [:ul {:className "list pl0 mt0 measure center"}
        (map (fn [{:keys [category shopping-items]}]
               (map (partial shopping-item open-count) shopping-items))
             shopping-list)]]]
     (when (not add-goods-modal-shown?) (add-button goods))
     (add-goods-modal goods open-category)]))

;; -------------------------
;; Initialize app
(.addEventListener js/window "load"
                   #(do
                      (reagent/render [main] (.getElementById js/document "app"))
                      (make-websocket! (str (if (= (-> js/document .-location .-protocol) "https:") "wss://" "ws://")
                                            (-> js/document .-location .-hostname) ":"
                                            "3000" "/ws"))))
