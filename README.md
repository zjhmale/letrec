# letrec

functions, lazy sequences, delays and the like can refer to other bindings regardless of the order in which they appear in the letrec form.

[![Build Status](https://travis-ci.org/zjhmale/uml.svg?branch=master)](https://travis-ci.org/zjhmale/uml)

## Installation

[![Clojars Project](https://clojars.org/zjhmale/letrec/latest-version.svg)](https://clojars.org/zjhmale/letrec)

## Usage

```clojure
:dependencies [[zjhmale/letrec "0.1.0"]]
```

## Examples

```clojure
(require [letrec.sweet :refer :all])

(letrec [fact (fn [n]
                (if (= n 1)
                  1
                  (* n (fact (dec n)))))]
  (fact 5))
```

## License

Copyright © 2015 Michał Marczyk

Distributed under the Eclipse Public License, the same as Clojure.
