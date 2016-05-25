(ns ^{:author "Dragan Djuric"}
  uncomplicate.clojurecl.legacy
  "Legacy alternatives for the functions from the `core` namespaces.
  If you need to use functions that were removed from the latest standard
  look for them here. Usually, they will be the same or similar to core
  functions, but will be suffixed by the largest version number that they
  support. Notable example is the [[command-queue-1]] function that is
  required if your platform does not support at least OpenCL 2.0."
  (:require [uncomplicate.clojurecl
             [constants :refer :all]
             [core :refer :all]
             [utils :refer [with-check with-check-arr mask error]]])
  (:import [org.jocl CL]))

(defn command-queue-1*
  "Creates a host or device command queue on a specific device.

  ** If you need to support legacy OpenCL 1.2 or earlier platforms,
  you MUST use this  function instead of [command-queue*], which is for
  OpenCL 2.0 and higher. What is important is the version of the platform,
  not the devices.**

  Arguments are:

  * `ctx` - the `cl_context` for the queue;
  * `device` - the `cl_device_id` for the queue;
  * `size` - the size of the (on device) queue;
  * `properties` - long bitmask containing properties, defined by the OpenCL
  standard are available as constants in the org.jocl.CL class.

  This is a low-level version of [[command-queue-1]].

  If called with invalid context or device, throws `ExceptionInfo`.

  See https://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clCreateCommandQueue.html,
  http://www.jocl.org/doc/org/jocl/CL.html#clCreateCommandQueue-org.jocl.cl_context-org.jocl.cl_device_id-long-int:A-

  Examples:
      (command-queue-1* ctx dev 524288  (bit-or CL/CL_QUEUE_PROFILING_ENABLED
                                                CL/CL_QUEUE_ON_DEVICE))
      (command-queue-1* ctx dev CL/CL_QUEUE_PROFILING_ENABLED)
  "
  ([ctx device ^long properties]
   (command-queue-1* ctx device 0 properties))
  ([ctx device ^long size ^long properties]
   (let [err (int-array 1)
         res (CL/clCreateCommandQueue ctx device properties err)]
     (with-check-arr err res))))

(defn command-queue-1
  "Creates a host or device command queue on a specific device.

  ** If you need to support legacy OpenCL 1.2 or earlier platforms,
  you MUST use this  function instead of [command-queue], which is for
  OpenCL 2.0 and higher. What is important is the version of the platform,
  not the devices.**

  Arguments are:

  * `ctx` - the `cl_context` for the queue;
  * `device` - the `cl_device_id` for the queue;
  * `x` - if integer, the size of the (on device) queue, otherwise treated
  as property;
  * `properties` - additional optional keyword properties: `:profiling`,
  `:queue-on-device`, `:out-of-order-exec-mode`, and `queue-on-device-default`;

  **Needs to be released after use.**

  See also [[command-queue-1*]].

  If called with invalid context or device, throws `ExceptionInfo`.
  If called with any invalid property, throws NullPointerexception.

  See https://www.khronos.org/registry/cl/sdk/2.0/docs/man/xhtml/clCreateCommandQueueWithProperties.html,
  https://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clCreateCommandQueue.html,
  http://www.jocl.org/doc/org/jocl/CL.html#clCreateCommandQueueWithProperties-org.jocl.cl_context-org.jocl.cl_device_id-org.jocl.cl_queue_properties-int:A-
  http://www.jocl.org/doc/org/jocl/CL.html#clCreateCommandQueue-org.jocl.cl_context-org.jocl.cl_device_id-long-int:A-

  Examples:

       (command-queue-1 ctx)
       (command-queue-1 ctx dev)
       (command-queue-1 ctx dev :profiling)
       (command-queue-1 ctx dev 524288 :queue-on-device)
  "
  ([ctx device x & properties]
   (if (integer? x)
     (command-queue-1* ctx device x
                       (mask cl-command-queue-properties properties))
     (command-queue-1* ctx device 0
                       (mask cl-command-queue-properties x properties))))
  ([ctx device]
   (command-queue-1* ctx device 0 0))
  ([device]
   (command-queue-1* *context* device 0 0)))

(defmacro with-default-1
  "Dynamically binds [[*platform*]], [[*context*]] and [[*command-queue]]
  to the first of the available platforms, the context containing the first
  device of that platform, and the queue on the device in that context.
  Supports pre-2.0 platforms."
  [& body]
  `(with-platform (first (platforms))
     (let [dev# (first (sort-by-cl-version (devices)))]
       (with-context (context [dev#])
         (with-queue (command-queue-1 dev#)
           ~@body)))))
