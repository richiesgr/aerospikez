# Working with Key Value Store

All operations are a Scalaz `Task`

## Write Record Operations

- put
```scala
// put(<key name>, <a value>)
set.put("one", 1).run                            // Unit


// put(<key name>, <a value>, <bin name>)
set.put("number", 1, "mult identity").run        // Unit


// put(<key name>, <one or more Bin(<bin name>, <a value>)>)
import aerospikez.Bin
set.put("example1", Bin("one", 1), Bin("two", 2)).run
```

## Read Record Operations

- get
```scala
// get(<key name>)
set.put("one", 1).run                            // Unit
set.get("one").run                               // Some(1)


// get(<key name>, <bin name>)
val id_user = scala.util.Random.nextInt()
set.put(
  id_user,
  Map("full name" -> "Chuck Norris", "age" -> 74),
  "personal info"
).run                                            // Unit
set.get(id_user, "personal info").run            // Some(Map(full name -> Chuck Norris, age -> 74))


// get(Keys(<one or more key name>))
import aerospikez.Keys
set.put("two", 2).run                            // Unit
set.get(Keys("one", "two")).run                  // Map(one -> 1, two -> 2)

// get(Keys(<one or more key name>), <bin name>)
val user_1 = scala.util.Random.nextInt()
val user_2 = user_1 + 1
set.put(user_1, "Bruce Lee", "name").run         // Unit
set.put(user_2, "Chuck Norris", "name").run      // Unit
set.get(Keys(user_1, user_2), "name").run        // Map(1893 -> Bruce Lee, 1894 -> Chuck Norris)


// get(<key name>, Bins(<one or more bin name>))
import aerospikez.Bins
set.put("num", 0, "add id").run                  // Unit
set.put("num", 1, "mult id").run                 // Unit
set.get("num", Bins("add id", "mult id")).run    // Map(add id -> 0, mult id -> 1)


// get(Keys(<one or more key name>), Bins(<one or more bin name>))
set.get(Keys("num"), Bins("add id")).run         // Map(num -> Map(add id -> 0))
```

- getHeader
```scala
// getHeader(<key name>)
set.getHeader("two").run                         // Some((1,148399023))

// getHeader(Keys(<one or more key name>))
set.getHeader(Keys("one", "two")).run            // Map(one -> (5,148398731), two -> (1,148399023))
```

## Existence Check Operations

- exists
```scala
// exists(<key name>)
set.exists("one").run                            // true
set.exists("three").run                          // false


// exists(Keys(<one or more key name>)
set.exists(Keys("one", "three")).run             // Map(one -> true, three -> false)
```

## Touch Operations

- touch
```scala
set.getHeader("one").run                         // Some((5,148398731))
// touch(<key name>)
set.touch("one").run                             // Unit
set.getHeader("one").run                         // Some((6,148399622))
```

## Delete Operations

- delete
```scala
// delete(<key name>)
set.exists("one").run                            // true
set.delete("one").run                            // Unit
set.exists("one").run                            // false
```

## Arithmetic Operations

- add
```scala
// add(<key name>, <a Int value>)
set.put("two", 1).run                            // Unit
set.add("two", 1).run                            // Unit
set.get("two").run                               // Some(2)
```

## String Operations

- append
```scala
// append(<key name>, <a String value>)
set.put("example1", "hello").run                 // Unit
set.append("example1", " world!").run            // Unit
set.get("example1").run                          // Some(hello world!)
```

- prepend
```scala
// prepend(<key name>, <a String value>)
set.put("example2", "world!").run                // Unit
set.prepend("example2", "hello ").run            // Unit
set.get("example2").run                          // Some(hello world!)
```

## Generic Database Operations

- operate
```scala
import aerospikez.Operations._

// operate(<key name>, <one or more operations>)
set.operate("numbers", put("one", 1), put("two", 2), put("three", 3), get("two")).run
// Some(2)

set.operate("names", put("agent1", "James"), put("agent2", "Jack")).run
// Some(())

set.operate("names", append("agent1", " Bond"), append("agent2", " Bauer"), get("agent1")).run
// Some(James Bond)
```
**Note:** Aerospike support only a write operation in a same bin, this will no check for this library for performance reason:
```scala
scala> set.operate("example", put("num", 1), add("num", 10)).run
com.aerospike.client.AerospikeException: Error Code 4: Parameter error
  at com.aerospike.client.async.AsyncRead.parseResult(AsyncRead.java:93)
  at com.aerospike.client.async.AsyncSingleCommand.read(AsyncSingleCommand.java:67)
  at com.aerospike.client.async.SelectorManager.processKey(SelectorManager.java:164)
  at com.aerospike.client.async.SelectorManager.runCommands(SelectorManager.java:108)
  at com.aerospike.client.async.SelectorManager.run(SelectorManager.java:69)
```
