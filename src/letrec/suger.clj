(ns letrec.suger)

(def car first)
(def cdr rest)
(def cadr #(car (cdr %)))
(def cddr #(cdr (cdr %)))

(defn let-vars
  [expr]
  (let [pairs (cadr expr)]
    (map car pairs)))

(defn let-vals
  [expr]
  (let [pairs (cadr expr)]
    (map cadr pairs)))

(defn let-body
  [expr]
  (cddr expr))

(defn make-lambda
  [params body]
  (cons 'lambda (cons params body)))

(defn let->lambda
  [expr]
  (let [vars (let-vars expr)
        vals (let-vals expr)
        body (let-body expr)]
    (cons (make-lambda vars body)
          vals)))


