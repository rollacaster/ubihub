(ns shopping-list.core-test
  (:require [clojure.test :refer :all]
            [shopping-list.core :refer :all]))

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
