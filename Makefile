repl:
	clojure -A:repl -e '(require (quote cljm.core)) (cljm.core/-main)' \
			-m nrepl.cmdline --middleware [cider.nrepl/cider-middleware]
