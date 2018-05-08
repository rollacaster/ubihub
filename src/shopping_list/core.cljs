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
(defonce add-modal-shown? (atom false))

;; -------------------------
;; Queries
(defn find-item
  [shopping-list id]
  (second (first (filter #(= (first %) id) shopping-list))))

(defn get-item
  [app-db id]
  (let [{:keys [shopping-list goods]} app-db
        listItem (find-item shopping-list id)
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
(go (let [response (<! (http/get "/shopping-list"
                                 {:with-credentials? false}))]
      (reset! app-db (:body response))))


(defn add-item
  [goodId]
  (go (let [res (<! (http/post "/shopping-list"
                               {:edn-params {:goodId goodId}
                                :with-credentials? false}))]
      (reset! app-db (:body res))
      (.scrollTo js/window 0 0))))


(defn increase-quantity
  [goodId]
  (go (let [res (<! (http/post (str "/shopping-list/" goodId "/increase")
                               {:with-credentials? false}))]
      (reset! app-db (:body res)))))

(defn decrease-quantity
  [goodId]
  (go (let [res (<! (http/post (str "/shopping-list/" goodId "/decrease")
                               {:with-credentials? false}))]
      (reset! app-db (:body res)))))

(defn remove-item
  [goodId]
  (go (let [res (<! (http/delete (str "/shopping-list/" goodId)
                                 {:with-credentials? false}))]
      (reset! app-db (:body res)))))

(defn toogle-modal
  []
  (let [body (.-body js/document)
        overflow (.-overflow (.-style body))]
    (if (= overflow "hidden")
      (set! (.-overflow (.-style body)) "")
      (set! (.-overflow (.-style body)) "hidden")))
  (swap! add-modal-shown? not))

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

(defn item
  [item]
  [:li {:key (:id item) :class "flex items-center lh-copy pa3 bb b--black-10"}
   [:input {:id (:id item)
            :type "checkbox"
            :checked false
            :on-change #(remove-item (:id item))}]
   [:label {:for (:id item) :class "f3 db black-70 pl3 flex-auto"} (:good item)]
   (quantity-counter (:id item) (:quantity item))])

(defn add-modal
  [goods]
  [:div
   {:class (str "w-100 vh-100 bg-white absolute flex-column justify-between ")
    :style {:transform (str "translateX(" (if @add-modal-shown? 0 375) "px)")
            :transition "transform 0.5s"
            :top (.-scrollY js/window)}}
   [:div {:class "center pa3"}
    [:ul {:class "list ph3 pv4"}
     (map (fn [good] [:li {:class "dib mr1 mb2"
                           :key (first good)}
                      [:button {:class "f4 b db pa2 dim dark-gray ba b--black-20 bg-white"
                                :on-click #(do (add-item (first good)) (toogle-modal))} (-> good second :name)]])
          goods)]]
   [:div {:class "flex justify-center"}
    [:button {:class "f5 pa2 mb2 ba white b--black-20 bg-black"
              :on-click toogle-modal} "back"]]])

(defn add-button
  [goods]
  (when (> (count goods) 0)
    [:div {:style {:position "sticky"} :class "flex justify-end bottom-0 right-0 mr3"}
     [:button {:class "f2 br-100 h3 w3 mb2 white bg-mid-gray shadow-5"
               :on-click toogle-modal} "+"]]))

(defn main []
  (let [app-db @app-db
        add-modal-shown? @add-modal-shown?
        goods (:goods (filter-goods app-db))]
    [:div {:class (str "test " (when add-modal-shown? "m0 h-100 overflow-hidden"))}
     [:div {:class "relative sans-serif mw5 center pa3"}
      [:ul {:class "list pl0 mt0 measure center"}
       (map (comp item #(get-item app-db (first %))) (:shopping-list app-db))]]
     (add-button goods)
     (add-modal goods)]))


;; -------------------------
;; Initialize app
(reagent/render [main] (.getElementById js/document "app"))

