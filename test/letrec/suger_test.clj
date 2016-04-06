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
              (+ x y)))))
  (testing "letrec -> let"
    (is= (letrec->let '(letrec ((fact (lambda (n)
                                              (if (= n 1)
                                                1
                                                (* n (fact (- n 1)))))))
                               (fact 10)))
         '(let ((fact *unassigned*))
            (set! fact (lambda (n)
                               (if (= n 1)
                                 1
                                 (* n (fact (- n 1))))))
            (fact 10)))
    (is= (letrec->let '(letrec ([is-even? (lambda (n)
                                                  (or (zero? n)
                                                      (is-odd? (sub1 n))))]
                                 [is-odd? (lambda (n)
                                                  (and (not (zero? n))
                                                       (is-even? (sub1 n))))])
                               (is-odd? 11)))
         '(let ((is-even? *unassigned*)
                 (is-odd? *unassigned*))
            (set! is-even? (lambda (n) (or (zero? n) (is-odd? (sub1 n)))))
            (set! is-odd? (lambda (n) (and (not (zero? n)) (is-even? (sub1 n)))))
            (is-odd? 11))))
  (testing "fix point combinator"
    (let [evens (fn [k] (letrec
                          ((even (fn [x] (if (= x 0)
                                           true
                                           (odd (- x 1)))))
                            (odd (fn [x] (if (= x 0)
                                           false
                                           (even (- x 1))))))
                          (filter even (range k))))]
      (is= (evens 10) '(0 2 4 6 8)))))
