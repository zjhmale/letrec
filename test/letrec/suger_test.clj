(ns letrec.suger-test
  (:require [acolfut.sweet :refer :all]
            [letrec.suger :refer :all]))

(deftest suger-test
  (testing "let -> lambda"
    (is= (let->lambda '(let ((a 1) (b 2)) (+ a b)))
         '((lambda (a b) (+ a b)) 1 2)))
  (testing "let* -> nested let"
    (is= (let*->nested-lets '(let* ((x 3)
                                     (y (+ x 1)))
                               (+ x y)))
         '(let ((x 3))
            (let ((y (+ x 1)))
              (+ x y))))))
