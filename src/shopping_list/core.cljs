(ns shopping-list.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)
;; -------------------------
;; Data

(defonce app-db (atom '{:goods {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana"}
                            "2dd89b67-3156-4c9e-8a44-7e4523e75199" {:name "Milk"}
                            "dbea6f63-1004-4c9c-8d28-b84c866df474" {:name "Cheese"}}
                    :shopping-list {}}))
(defonce add-modal-shown? (atom false))

;; -------------------------
;; Queries

(defn get-item
  [app-db id]
  (let [{:keys [shopping-list goods]} app-db
        listItem (get shopping-list id)
        good (get goods (:good listItem))]
    (merge {:id id} (assoc listItem :good (:name good)))))

(defn remove-shopping-item
  [app-db id]
  (merge app-db {:shopping-list (dissoc (:shopping-list app-db) id)}))

;; -------------------------
;; Actions
;; (go (let [response (<! (http/get "http://localhost:3449/shopping-list"
;;                                  {:with-credentials? false}))]
;;       (println (read-string (:body response)))
;;       (reset! app-db (:body response))))

(defn add-item
  [goodId]
  ;; (http/post "http://localhost:3449/shopping-list" {:edn-params {:goodId goodId}})
  (swap! app-db #(assoc % :shopping-list (conj (:shopping-list %) (hash-map (str (random-uuid)) {:quantity 1 :good goodId})))))


(defn increase-quantity
  [id]
  (swap! app-db #(update-in % [:shopping-list id :quantity] (comp (fn [x] (min 9 x)) inc))))

(defn decrease-quantity
  [id quantity]
  (swap! app-db #(if (> quantity  1)
                   (update-in % [:shopping-list id :quantity] dec)
                   (remove-shopping-item % id))))

(defn remove-item
  [id]
  (swap! app-db #(remove-shopping-item % id)))

(defn toogle-modal
  []
  (swap! add-modal-shown? not))

;; -------------------------
;; Views
(defn quantity-button
  [label on-click]
  [:button
    {:style {:border "none" :padding 0 :margin 0 :width 11 :outline "none"}
     :on-click on-click}
    label])

(defn quantity-counter
  [id quantity]
  [:div {:style {:display "flex" :flex-direction "column" :align-items "center"}}
   (quantity-button "🔼" #(increase-quantity id))
   [:span quantity]
   (quantity-button "🔽" #(decrease-quantity id quantity))])

(defn item
  [item]
  [:li {:key (:id item) :style {:width 150
                                :display "flex"
                                :justify-content "space-between"
                                :align-items "center"
                                :margin-bottom 20}}
   [:div
    [:label
     [:input {:type "checkbox"
              :checked false
              :style {:margin-right 20}
              :on-change #(remove-item (:id item))}]
     (:good item)]]
   (quantity-counter (:id item) (:quantity item))])

(defn add-modal
  []
  [:div
   [:button {:style {:border "1px solid grey"}
             :on-click toogle-modal} "➕"]
   [:div
    {:style {:width 500 :height 300
             :position "absolute"
             :top "0%"
             :background-color :white
             :border "1px solid grey"
             :display (if @add-modal-shown? "flex" "none")
             :flex-direction "column"
             :justify-content "space-between"}}
    [:div
     [:h2 "Goods"]
     [:ul {:style {:list-style "none"}}
      (map (fn [good] [:li {:key (first good)}
                          [:button {:on-click #(do (add-item (first good)) (toogle-modal))} (-> good second :name)]])
           (:goods @app-db))]]
    [:div {:style {:display "flex" :justify-content "center"}}
     [:button {:on-click toogle-modal} "close"]]]])

(defn main []
  (let [app-db @app-db]
    [:div {:style {:position "relative"}}
     [:h2 "Groceries"]
     [:ul (map (comp item #(get-item app-db (first %))) (:shopping-list app-db))]
     (add-modal)]))

(defn about-page []
  [:div [:h2 "About shopping-list"]
   [:div [:a {:href "/"} "go to the home page"]]])

;; -------------------------
;; Initialize app
(reagent/render [main] (.getElementById js/document "app"))

