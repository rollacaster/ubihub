(ns shopping-list.core-test
  (:require [clojure.test :refer :all]
            [shopping-list.core :refer :all]))

(deftest core
  (testing "add-shopping-item"
    (is (=
         (add-shopping-item "59b79451-6d22-44ec-93fc-8336bed7a46c"
                            "8280b3f8-e14a-45d9-be46-09ffb44b8db9"
                            {:shopping-list []
                             :goods
                             {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana"}}})
         {:shopping-list
          [["59b79451-6d22-44ec-93fc-8336bed7a46c" {:quantity 1, :good "8280b3f8-e14a-45d9-be46-09ffb44b8db9"}]]
          :goods
          {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana"}}})))
  (testing "remove-shopping-item"
    (is (=
         (remove-shopping-item "59b79451-6d22-44ec-93fc-8336bed7a46c"
                               {:shopping-list [["59b79451-6d22-44ec-93fc-8336bed7a46c" {:quantity 1, :good "8280b3f8-e14a-45d9-be46-09ffb44b8db9"}]]
                                :goods
                                {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana"}}})
         {:shopping-list []
          :goods {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana"}}})))
  (testing "increase-quantity"
    (is (=
         (increase-quantity "59b79451-6d22-44ec-93fc-8336bed7a46c"
                            {:shopping-list [["59b79451-6d22-44ec-93fc-8336bed7a46c" {:quantity 1, :good "8280b3f8-e14a-45d9-be46-09ffb44b8db9"}]]
                             :goods
                             {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana"}}})
         {:shopping-list [["59b79451-6d22-44ec-93fc-8336bed7a46c" {:quantity 2, :good "8280b3f8-e14a-45d9-be46-09ffb44b8db9"}]]
          :goods
          {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana"}}})))
  (testing "decrease-quantity"
    (testing "<=1 quantity"
      (is (=
           (decrease-quantity "59b79451-6d22-44ec-93fc-8336bed7a46c"
                              {:shopping-list [["59b79451-6d22-44ec-93fc-8336bed7a46c" {:quantity 1, :good "8280b3f8-e14a-45d9-be46-09ffb44b8db9"}]]
                               :goods
                               {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana"}}})
           {:shopping-list []
            :goods
            {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana"}}}))))
  (testing ">1 quantity"
      (is (=
           (decrease-quantity "59b79451-6d22-44ec-93fc-8336bed7a46c"
                              {:shopping-list [["59b79451-6d22-44ec-93fc-8336bed7a46c" {:quantity 2, :good "8280b3f8-e14a-45d9-be46-09ffb44b8db9"}]]
                               :goods
                               {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana"}}})
           {:shopping-list [["59b79451-6d22-44ec-93fc-8336bed7a46c" {:quantity 1, :good "8280b3f8-e14a-45d9-be46-09ffb44b8db9"}]]
            :goods
            {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana"}}})))
  (testing "find-shopping-item"
    (is (=
         (find-shopping-item "59b79451-6d22-44ec-93fc-8336bed7a46c"
                            {:shopping-list [["59b79451-6d22-44ec-93fc-8336bed7a46c" {:quantity 2, :good "8280b3f8-e14a-45d9-be46-09ffb44b8db9"}]]
                             :goods
                             {"8280b3f8-e14a-45d9-be46-09ffb44b8db9" {:name "Banana"}}})
         ["59b79451-6d22-44ec-93fc-8336bed7a46c" {:quantity 2, :good "8280b3f8-e14a-45d9-be46-09ffb44b8db9"}]))))
