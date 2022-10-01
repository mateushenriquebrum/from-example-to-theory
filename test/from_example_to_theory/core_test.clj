(ns from-example-to-theory.core-test
  (:require [clojure.test :refer :all]
            [from-example-to-theory.core :refer :all]
            [clojure.test.check.clojure-test :refer (defspec)]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]))

(deftest making
  (testing "should make dollar"
    (is (= (:currency (dollar 0)) :$)))
  (testing "should make euro"
    (is (= (:currency (euro 0)) :€))))

(deftest operations
  (testing "should multiply"
    (is (= (multiply 2 (dollar 10)) (dollar 20)))
    (is (= (multiply 5 (dollar 10)) (dollar 50)))
    (is (= (multiply 5 (euro 10)) (euro 50))))
  (testing "should divide"
    (is (= (divide 2 (dollar 10)) (dollar 5)))))

(deftest exchanges
  (testing "convert"
    (let [bank {:$ 100 :€ 102}]
      (is (= (exchange bank (dollar 300) :€) (euro 306)))
      (is (= (exchange bank (euro 306) :$) (dollar 300)))
      (is (= (exchange bank (exchange bank (euro 100) :$) :€) (euro 100))))))

(defn int-non-zero []
  (gen/such-that #(> % 0) gen/nat))

(defn currencies []
  (gen/shuffle [:$ :€]))

(defn amounts []
  (gen/fmap #(* 100 %) gen/nat))

(defspec division-as-identity-for-multiply 100
  (prop/for-all [a gen/nat
                 t (int-non-zero)]
                (= (divide t (multiply t (euro a))) (euro a))))

(defspec convertions-as-identity-for-conversions 100
  (prop/for-all [from-rate (int-non-zero)
                 to-rate (int-non-zero)
                 amount (amounts)
                 currencies (currencies)]
                (let [from-currency (first currencies)
                      to-currency (second currencies)
                      money (make from-currency amount)
                      bank {from-currency from-rate to-currency to-rate}]
                  (= (exchange bank (exchange bank money to-currency) from-currency) money)
                  (= (exchange bank (exchange bank money from-currency) from-currency) money))))

(defspec excludents-for-conversions 100
  (prop/for-all [from-rate (int-non-zero)
                 to-rate (int-non-zero)
                 amount (amounts)
                 currencies (currencies)]
                (let [from-currency (first currencies)
                      to-currency (second currencies)
                      money (make from-currency amount)
                      bank {from-currency from-rate to-currency to-rate}]
                  (if (or (= from-rate to-rate) (zero? (:amount money)))
                    (= (:amount (exchange bank money from-currency)) (:amount (exchange bank money to-currency)))
                    (not= (:amount (exchange bank money from-currency)) (:amount (exchange bank money to-currency)))))))
