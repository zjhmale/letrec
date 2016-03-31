## use destructure function to handle

```clojure
(letrec [[ev? od?]
                [(fn [n] (if (zero? n) true (od? (dec n))))
                 (fn [n] (if (zero? n) false (ev? (dec n))))]]
         (ev? 10))
         
(letrec [[x y :as fibs]
                (cons 0 (cons 1 (lazy-seq (map + fibs (rest fibs)))))]
         [x y (take 10 fibs)])
         
(letrec [[f & fibs] (cons 0 (cons 1 (lazy-seq (map + (cons f fibs) fibs))))]
         (take 10 (cons f fibs)))
```