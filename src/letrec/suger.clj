(ns letrec.suger
  (require [letrec.sweet :as sweet]))

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
  (sweet/letrec [expand-clauses (fn [bindings body]
                            (if (or (nil? bindings) (empty? bindings))
                              body
                              (make-let (list (car bindings))
                                        (expand-clauses (cdr bindings)
                                                        body))))]
    (expand-clauses (cadr expr) (caddr expr))))

(defn letrec-bindings
  [expr]
  (cadr expr))

(defn letrec-body
  [expr]
  (cddr expr))

(defn binding-var
  [binding]
  (car binding))

(defn binding-val
  [binding]
  (cadr binding))

(defn letrec->let
  [expr]
  (concat (concat (list 'let (map (fn [binding]
                                    (list (binding-var binding) '*unassigned*))
                                  (letrec-bindings expr)))
                  (map (fn [binding]
                         (list 'set!
                               (binding-var binding)
                               (binding-val binding)))
                       (letrec-bindings expr))
                  (letrec-body expr))))

(defn Y* [& fl]
  (map (fn [x] (x))
       ((fn [x] (x x))
         (fn [p]
           (map
             (fn [f]
               (fn []
                 (apply f
                        (map
                          (fn [ff]
                            (fn [& y]
                              (apply (ff) y)))
                          (p p)))))
             fl)))))

(defmacro letrec [heads & bodies]
  (let [vars (map first heads)
        defs (map rest heads)
        lam  (fn [x] `(fn [~@vars] ~@x))
        fixp (gensym)]
    `(let [~fixp (Y* ~@(map lam defs))]
       (apply ~(lam bodies) ~fixp))))

(defn Y [f]
  ((fn [x]
     (f (fn [y]
          ((x x) y))))
    (fn [x]
      (f (fn [y]
           ((x x) y))))))
