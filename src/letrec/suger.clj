(ns letrec.suger
  (require [letrec.sweet :refer :all]))

(def car first)
(def cdr rest)
(def cadr #(car (cdr %)))
(def cddr #(cdr (cdr %)))
(def caddr #(car (cdr (cdr %))))

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

(defn make-let
  [bindings body]
  (cons 'let (cons bindings (list body))))

(defn let*->nested-lets
  [expr]
  (letrec [expand-clauses (fn [bindings body]
                            (if (or (nil? bindings) (empty? bindings))
                              body
                              (make-let (list (car bindings))
                                        (expand-clauses (cdr bindings)
                                                        body))))]
    (expand-clauses (cadr expr) (caddr expr))))

