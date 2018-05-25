(ns shopping-list.core-test
  (:require [clojure.test :refer :all]
            [shopping-list.core :refer :all]))

(deftest shopping-items
  (testing "add-shopping-item"
    (is (=
         (reducer {:shopping-list []
                   :goods
                   {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana"}}}
                  {:type :add-shopping-item
                   :uuid "59b79451-6d22-44ec-93fc-8336bed7a46c"
                   :good "8280b3f8-e14a-45d9-be46-09ffb44b8db9"})
         {:shopping-list
          [["59b79451-6d22-44ec-93fc-8336bed7a46c" {:quantity 1, :good "8280b3f8-e14a-45d9-be46-09ffb44b8db9"}]]
          :goods
          {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana"}}})))
  (testing "remove-shopping-item"
    (is (=
         (reducer {:shopping-list [["59b79451-6d22-44ec-93fc-8336bed7a46c" {:quantity 1, :good "8280b3f8-e14a-45d9-be46-09ffb44b8db9"}]]
                   :goods
                   {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana"}}}
                  {:type :remove-shopping-item :uuid "59b79451-6d22-44ec-93fc-8336bed7a46c"})
         {:shopping-list []
          :goods {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana"}}})))
  (testing "increase-quantity"
    (is (=
         (reducer {:shopping-list [["59b79451-6d22-44ec-93fc-8336bed7a46c" {:quantity 1, :good "8280b3f8-e14a-45d9-be46-09ffb44b8db9"}]]
                   :goods
                   {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana"}}}
                  {:type :increase-quantity :uuid "59b79451-6d22-44ec-93fc-8336bed7a46c"})
         {:shopping-list [["59b79451-6d22-44ec-93fc-8336bed7a46c" {:quantity 2, :good "8280b3f8-e14a-45d9-be46-09ffb44b8db9"}]]
          :goods
          {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana"}}})))
  (testing "decrease-quantity"
    (testing "<=1 quantity"
      (is (=
           (reducer {:shopping-list [["59b79451-6d22-44ec-93fc-8336bed7a46c" {:quantity 1, :good "8280b3f8-e14a-45d9-be46-09ffb44b8db9"}]]
                     :goods
                     {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana"}}}
                    {:type :decrease-quantity :uuid "59b79451-6d22-44ec-93fc-8336bed7a46c"})
           {:shopping-list [["59b79451-6d22-44ec-93fc-8336bed7a46c" {:quantity 1, :good "8280b3f8-e14a-45d9-be46-09ffb44b8db9"}]]
            :goods
            {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana"}}}))))
  (testing ">1 quantity"
      (is (=
           (reducer {:shopping-list [["59b79451-6d22-44ec-93fc-8336bed7a46c" {:quantity 2, :good "8280b3f8-e14a-45d9-be46-09ffb44b8db9"}]]
                     :goods
                     {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana"}}}
                    {:type :decrease-quantity :uuid "59b79451-6d22-44ec-93fc-8336bed7a46c"})
           {:shopping-list [["59b79451-6d22-44ec-93fc-8336bed7a46c" {:quantity 1, :good "8280b3f8-e14a-45d9-be46-09ffb44b8db9"}]]
            :goods
            {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana"}}}))))

(deftest goods
  (testing "add-goods"
    (is (=
           (reducer {:shopping-list [] :goods {}}
                    {:type :add-good :uuid "8280b3f8-e14a-45d9-be46-09ffb44b8db9" :name "Banana"})
           {:shopping-list []
            :goods {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana"}}}))))
