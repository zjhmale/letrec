# letrec

[![Build Status](https://travis-ci.org/zjhmale/uml.svg?branch=master)](https://travis-ci.org/zjhmale/uml)

## Usage

```clojure
(letrec [fact (fn [n]
                (if (= n 1)
                  1
                  (* n (fact (dec n)))))]
  (fact 5))
```

## License

Copyright © 2015 Michał Marczyk

Distributed under the Eclipse Public License, the same as Clojure.
