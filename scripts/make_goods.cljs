(ns scripts.make-good
  (:require [planck.core :refer [line-seq with-open spit]]
            [planck.io :as io]))


(with-open [rdr (io/reader "./goods.txt")
            wrt (io/writer "./goods.edn")]
  (let [goods (atom [])]
    (doseq [line (line-seq rdr)]
      (swap! goods #(conj % {:type :add-good :uuid (str (random-uuid)) :name line})))
    (spit wrt @goods)))


