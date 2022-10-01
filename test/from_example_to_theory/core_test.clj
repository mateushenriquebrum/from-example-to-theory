(ns from-example-to-theory.core-test
  (:require [clojure.test :refer :all]
            [from-example-to-theory.core :refer :all]
            [clojure.test.check.clojure-test :refer (defspec)]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]))

(deftest amounts
  (testing "should get amount"
    (is (= (amount (dollar 10)) 10))))

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

(defspec division-as-identity-for-multiply 100
  (prop/for-all [a gen/nat
                 t (gen/such-that #(> % 0) gen/nat)]
                (= (divide t (multiply t (euro a))) (euro a))))

(defspec convertions-as-identity-for-conversions 100
  (let [bank {:$ 100 :€ 102}]
    (prop/for-all [amount (gen/fmap #(* 100 %) gen/nat)
                   from-currency (gen/elements [:$ :€])
                   to-currency (gen/elements [:$ :€])]
                  (let [money {:currency from-currency :amount amount}]
                    (= (exchange bank (exchange bank money to-currency) from-currency) money)))))
