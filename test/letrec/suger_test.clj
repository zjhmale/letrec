(ns letrec.suger-test
  (:require [acolfut.sweet :refer :all]
            [letrec.suger :refer :all]))

(deftest suger-test
  (testing "let -> lambda"
    (is= (let->lambda '(let ((a 1) (b 2)) (+ a b)))
         '((lambda (a b) (+ a b)) 1 2))))
