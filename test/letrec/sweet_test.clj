(ns letrec.sweet-test
  (:require [acolfut.sweet :refer :all]
            [letrec.sweet :refer :all]))

(deftest let-test
  (is= (letrec [fibs (cons 0 (cons 1 (lazy-seq (map + fibs (rest fibs)))))]
         (take 10 fibs))
       '(0 1 1 2 3 5 8 13 21 34))
  (is= (letrec [x 1
                y 'x]
         y)
       'x)
  (is= (letrec [ev? (fn [n] (if (zero? n) true (od? (dec n))))
                od? (fn [n] (if (zero? n) false (ev? (dec n))))]
         (ev? 11))
       false)
  (is= (letrec [xs (lazy-seq (filter even? ys))
                ys (range 10)]
         xs)
       '(0 2 4 6 8))
  (is= (letrec [y (delay x)
                x 1]
         (let [y (delay :foo)]
           (force y)))
       :foo)
  (is= (letrec [[ev? od?]
                [(fn [n] (if (zero? n) true (od? (dec n))))
                 (fn [n] (if (zero? n) false (ev? (dec n))))]]
         (ev? 10))
       true)
  (is= (letrec [[x y :as fibs]
                (cons 0 (cons 1 (lazy-seq (map + fibs (rest fibs)))))]
         [x y (take 10 fibs)])
       [0 1 '(0 1 1 2 3 5 8 13 21 34)])
  (is= (letrec [[f & fibs] (cons 0 (cons 1 (lazy-seq (map + (cons f fibs) fibs))))]
         (take 10 (cons f fibs)))
       '(0 1 1 2 3 5 8 13 21 34))
  (is= (letrec [y (delay x)
                x 1]
         (force y))
       1)
  (is= (letrec [y x
                x 1]
         y)
       nil)
  (is= (letrec [xs (filter even? (lazy-seq ys))
                ys (range)]
         (take 10 xs))
       '(0 2 4 6 8 10 12 14 16 18))
  (is= (letrec [xs (filter even? ys)
                ys (range)]
         (take 10 xs))
       '()))
