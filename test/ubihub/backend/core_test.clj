(ns ubihub.core-test
  (:require [clojure.test :refer :all]
            [ubihub.core :refer :all]))

(deftest shopping-items
  (testing "add-shopping-item"
    (is (=
         (reducer {:shopping-list {}}
                  {:type :add-shopping-item
                   :uuid "8280b3f8-e14a-45d9-be46-09ffb44b8db9"
                   :name "Banana"
                   :category "8442ab6f-69df-49ef-a1dd-a2125197e063"})
         {:shopping-list {"8280b3f8-e14a-45d9-be46-09ffb44b8db9"
                          {:name "Banana" :quantity 0 :category "8442ab6f-69df-49ef-a1dd-a2125197e063"}}})))
  (testing "remove-shopping-item"
    (is (=
         (reducer {:shopping-list
                   {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana" :quantity 2}}}
                  {:type :remove-shopping-item :uuid "8280b3f8-e14a-45d9-be46-09ffb44b8db9"})
         {:shopping-list {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana" :quantity 0}}})))
  (testing "increase-quantity"
    (testing "<9 quantity"
      (is (=
           (reducer {:shopping-list
                     {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana" :quantity 1}}}
                    {:type :increase-quantity :uuid "8280b3f8-e14a-45d9-be46-09ffb44b8db9"})
           {:shopping-list
            {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana" :quantity 2}}})))
    (testing ">=9 quantity"
      (is (=
           (reducer {:shopping-list
                     {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana" :quantity 9}}}
                    {:type :increase-quantity :uuid "8280b3f8-e14a-45d9-be46-09ffb44b8db9"})
           {:shopping-list
            {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana" :quantity 9}}}))))
  (testing "decrease-quantity"
    (testing "=1 quantity"
      (is (=
           (reducer {:shopping-list
                     {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana" :quantity 1}}}
                    {:type :decrease-quantity :uuid "8280b3f8-e14a-45d9-be46-09ffb44b8db9"})
           {:shopping-list
            {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana" :quantity 1}}})))
    (testing ">1 quantity"
      (is (=
           (reducer {:shopping-list
                     {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana" :quantity 2}}}
                    {:type :decrease-quantity :uuid "8280b3f8-e14a-45d9-be46-09ffb44b8db9"})
           {:shopping-list
            {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana" :quantity 1}}})))))

(deftest category
  (testing "add-category"
    (is (=
         (reducer {:categorys {}}
                  {:type :add-category :name "Obst" :uuid "55968d0e-1c1d-4303-a541-ada5b1b6261e"})
         {:categorys {"55968d0e-1c1d-4303-a541-ada5b1b6261e" {:name "Obst"}}}))))

(deftest denormalization
  (testing "goods & shoppinglist available"
    (is (=
         (denormalize-state
          {:categorys {"55968d0e-1c1d-4303-a541-ada5b1b6261e" {:name "Obst"}}
           :shopping-list {"8280b3f8-e14a-45d9-be46-09ffb44b8db9"
                           {:name "Banana" :quantity 0 :category "55968d0e-1c1d-4303-a541-ada5b1b6261e"}
                           "caf3d381-c200-4919-a787-8dc17976b2ca"
                           {:name "Apfel" :quantity 1 :category "55968d0e-1c1d-4303-a541-ada5b1b6261e"}}})
         {:goods [{:category "Obst" :goods [{:uuid "8280b3f8-e14a-45d9-be46-09ffb44b8db9" :name "Banana" :quantity 0}]}]
          :shopping-list [{:category "Obst" :shopping-items [{:uuid "caf3d381-c200-4919-a787-8dc17976b2ca" :name "Apfel" :quantity 1}]}]})))
  (testing "only goods"
    (is (=
         (denormalize-state
          {:categorys {"55968d0e-1c1d-4303-a541-ada5b1b6261e" {:name "Obst"}}
           :shopping-list {"8280b3f8-e14a-45d9-be46-09ffb44b8db9"
                           {:name "Banana" :quantity 0 :category "55968d0e-1c1d-4303-a541-ada5b1b6261e"}}})
         {:goods [{:category "Obst" :goods [{:uuid "8280b3f8-e14a-45d9-be46-09ffb44b8db9" :name "Banana" :quantity 0}]}]
          :shopping-list []}))))
