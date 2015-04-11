(ns uncomplicate.clojurecl.utils
  (:require [clojure.string :as str]
            [uncomplicate.clojurecl.constants :refer [dec-error]])
  (:import clojure.lang.ExceptionInfo))

;; ========= Bitfild masks ========================================

(defn mask ^long
  ([table flag1 flag2 flags]
   (apply bit-or (table flag1) (table flag2) (map table flags)))
  ([table flag flags]
   (apply bit-or 0 (table flag) (map table flags)))
  ([table flags]
   (apply bit-or 0 0 (map table flags))))

(defn unmask [table ^long mask]
  (filter identity
          (map (fn [[k v]]
                 (if (= 0 (bit-and mask (long v)))
                   nil
                   k))
               table)))

(defn unmask1 [table ^long mask]
  (some identity
        (map (fn [[k v]]
               (if (= 0 (bit-and mask (long v)))
                   nil
                   k))
             table)))

;; ========== Error handling ======================================

(defn error
  ([err-code details]
   (let [err (dec-error err-code)]
     (ex-info (format "OpenCL error: %s." err)
              {:name err :code err-code :type :opencl-error :details details})))
  ([err-code]
   (error err-code nil)))

(defmacro with-check-arr [err-code form]
  `(with-check (aget (ints ~err-code) 0) ~form))

(defmacro with-check [err-code form]
  `(if (= 0 ~err-code)
     ~form
     (throw (error ~err-code))))

(defmacro maybe [form]
  `(try ~form
         (catch ExceptionInfo ex-info#
           (if (= :opencl-error (:type (ex-data ex-info#)))
             ex-info#
             (throw ex-info#)))))
