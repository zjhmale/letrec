there is another definition of letrec

```clojure
(defmacro letrec-simple [bindings & body]
  (let [bcnt (quot (count bindings) 2)
        arrs (gensym "bindings_array")
        arrv `(make-array Object ~bcnt)
        bprs (partition 2 bindings)
        bssl (map first bprs)
        bsss (set bssl)
        bexs (map second bprs)
        arrm (zipmap bssl (range bcnt))
        btes (map #(walk/prewalk (fn [f]
                                   (if (bsss f)
                                     `(aget ~arrs ~(arrm f))
                                     f))
                                 %)
                  bexs)]
    `(let [~arrs ~arrv]
       ~@(map (fn [s e]
                `(aset ~arrs ~(arrm s) ~e))
              bssl
              btes)
       (let [~@(mapcat (fn [s]
                         [s `(aget ~arrs ~(arrm s))])
                       bssl)]
         ~@body))))
```

use destructure function to handle conditions below

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

symbol macro defines a form that replaces a symbol during macro expansion. so use `symbol-macrolet` to handle the symbols inside the recursive definitions

```clojure
(require [clojure.tools.macro :refer [mexpand-all]])

(mexpand-all '(letrec [y x
                       x 1]
                y))
;;=> (let*
      [letrec_bindings_array__6065 (clojure.core/make-array java.lang.Object 2)]
      (do
       (clojure.core/aset letrec_bindings_array__6065 0 (clojure.core/aget letrec_bindings_array__6065 1))
       (clojure.core/aset letrec_bindings_array__6065 1 1))
      (let* [y (clojure.core/aget letrec_bindings_array__6065 0) x (clojure.core/aget letrec_bindings_array__6065 1)] y))                 
      
;; symbol-macrolet will ignore quote reader macro
(mexpand-all '(letrec [y 'x
                       x 1]
                y))
;;=> (let*
      [letrec_bindings_array__6025 (clojure.core/make-array java.lang.Object 2)]
      (do (clojure.core/aset letrec_bindings_array__6025 0 (quote x)) (clojure.core/aset letrec_bindings_array__6025 1 1))
      (let* [y (clojure.core/aget letrec_bindings_array__6025 0) x (clojure.core/aget letrec_bindings_array__6025 1)] y))
      
;; if use letrec-simple
;;=> (let*
      [bindings_array6941 (clojure.core/make-array java.lang.Object 2)]
      (clojure.core/aset bindings_array6941 0 (quote (clojure.core/aget bindings_array6941 1)))
      (clojure.core/aset bindings_array6941 1 1)
      (let* [y (clojure.core/aget bindings_array6941 0) x (clojure.core/aget bindings_array6941 1)] y))
 
(mexpand-all '(letrec [y (delay x)
                       x 1]
               (force y)))
;;=> (let*
      [letrec_bindings_array__5934 (clojure.core/make-array java.lang.Object 2)]
      (do
       (clojure.core/aset
        letrec_bindings_array__5934
        0
        (new clojure.lang.Delay (fn* ([] (clojure.core/aget letrec_bindings_array__5934 1)))))
       (clojure.core/aset letrec_bindings_array__5934 1 1))
      (let*
       [y (clojure.core/aget letrec_bindings_array__5934 0) x (clojure.core/aget letrec_bindings_array__5934 1)]
       (force y)))
       
;; if use letrec-simple
;;=> (let*
      [letrec_bindings_array__5963 (clojure.core/make-array java.lang.Object 2)]
      (clojure.core/aset letrec_bindings_array__5963 0 (new clojure.lang.Delay (fn* ([] x))))
      (clojure.core/aset letrec_bindings_array__5963 1 1)
      (let*
       [y (clojure.core/aget letrec_bindings_array__5963 0) x (clojure.core/aget letrec_bindings_array__5963 1)]
       (force y)))
```
