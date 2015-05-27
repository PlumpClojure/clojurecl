(ns uncomplicate.clojurecl.core-test
  (:require [midje.sweet :refer :all]
            [uncomplicate.clojurecl.utils :refer :all]))

(facts
 "mask tests"
 (let [table {:a 1 :b 2 :c 4}]

   (mask table [:a :c]) => 5
   (mask table :a [:c]) => 5
   (mask table :a :c[]) => 5

   (mask table [:k]) => (throws NullPointerException)
   (mask {} [:a]) => (throws NullPointerException)))

(facts
 "unmask tests"
 (let [table {:a 1 :b 2 :c 4}]

   (unmask table 1) => '(:a)
   (unmask table 2) => '(:b)
   (unmask table 4) => '(:c)

   (unmask table 0) => '()
   (unmask table 3) => '(:a :b)
   (unmask table 5) => '(:a :c)
   (unmask table 7) => '(:a :b :c)

   (unmask table 10) => '(:b)

   (unmask nil 0) => '()))

(facts
 "unmask1 tests"
 (let [table {:a 1 :b 2 :c 4}]

   (unmask1 table 1) => :a
   (unmask1 table 2) => :b
   (unmask1 table 4) => :c

   (unmask1 table 0) => nil
   (unmask1 table 3) => :a
   (unmask1 table 5) => :a
   (unmask1 table 7) => :a

   (unmask1 table 10) => :b

   (unmask1 nil 0) => nil))

(facts
 "error tests"

 (ex-data (error 0))
 => {:code 0, :details nil, :name "CL_SUCCESS", :type :opencl-error}

 (ex-data (error 43))
 => {:code 43, :details nil, :name "UNKNOWN OpenCL ERROR!", :type :opencl-error}

 (ex-data (error 0 "Additional details"))
 => {:code 0, :details "Additional details", :name "CL_SUCCESS", :type :opencl-error})

(facts
 "with-check tests"
 (let [f (fn [x] (if x 0 -1))]
   (with-check (f 1) :success) => :success
   (with-check (f false) :success) => (throws clojure.lang.ExceptionInfo)))

(facts
 "with-check-arr tests"
 (let [f (fn [x ^ints err]
           (do (aset err 0 (if x 0 -1))
               x))
       err (int-array 1)]
   (let [res (f :success err)]
     (with-check-arr err res) => :success)
   (let [res (f false err)]
     (with-check-arr err res))) => (throws clojure.lang.ExceptionInfo))

(facts
 "maybe tests"
 (ex-data (maybe (throw (ex-info "Test Exception" {:data :test}))))
 => (throws clojure.lang.ExceptionInfo)

 (:type (ex-data (error -1 nil))) => :opencl-error)
