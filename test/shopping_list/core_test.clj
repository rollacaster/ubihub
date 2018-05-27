(ns shopping-list.core-test
  (:require [clojure.test :refer :all]
            [shopping-list.core :refer :all]))

(deftest shopping-items
  (testing "remove-shopping-item"
    (is (=
         (reducer {:goods
                   {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana" :quantity 2}}}
                  {:type :remove-shopping-item :uuid "8280b3f8-e14a-45d9-be46-09ffb44b8db9"})
         {:goods {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana" :quantity 0}}})))
  (testing "increase-quantity"
    (testing "<9 quantity"
      (is (=
           (reducer {:goods
                     {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana" :quantity 1}}}
                    {:type :increase-quantity :uuid "8280b3f8-e14a-45d9-be46-09ffb44b8db9"})
           {:goods
            {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana" :quantity 2}}})))
    (testing ">=9 quantity"
      (is (=
           (reducer {:goods
                     {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana" :quantity 9}}}
                    {:type :increase-quantity :uuid "8280b3f8-e14a-45d9-be46-09ffb44b8db9"})
           {:goods
            {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana" :quantity 9}}}))))
  (testing "decrease-quantity"
    (testing "=1 quantity"
      (is (=
           (reducer {:goods
                     {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana" :quantity 1}}}
                    {:type :decrease-quantity :uuid "8280b3f8-e14a-45d9-be46-09ffb44b8db9"})
           {:goods
            {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana" :quantity 1}}})))
    (testing ">1 quantity"
      (is (=
           (reducer {:goods
                     {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana" :quantity 2}}}
                    {:type :decrease-quantity :uuid "8280b3f8-e14a-45d9-be46-09ffb44b8db9"})
           {:goods
            {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana" :quantity 1}}})))))

(deftest goods
  (testing "add-goods"
    (is (=
           (reducer {:shopping-list [] :goods {}}
                    {:type :add-good :uuid "8280b3f8-e14a-45d9-be46-09ffb44b8db9" :name "Banana"})
           {:shopping-list []
            :goods {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana"}}}))))
