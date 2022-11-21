Gingham
=======

A Gradle plugin that generates type checked formatters for patterned Android string resources.

Usage
-----

Let's say you have an Android application or library module that contains the following ICU
formatted string resource:

```xml

<resources>
  <string name="order_description">
    {count, plural,
    =0 {{name} does not order any bagels}
    =1 {{name} orders an everything bagel}
    other {{name} orders # everything bagels}
    }
  </string>
</resources>
```

First, apply the Gingham plugin to the module:

```kotlin
plugins {
  id("app.cash.gingham")
}
```

Then, generate the type safe formatters by building the module:

```shell
./gradlew my-app:build
```

Finally, use the type safe formatters in your code:

```kotlin
import app.cash.gingham.getString

val orderDescription = resources.getString(
  FormattedStrings.order_description(
    count = 12,
    name = "Jobu Tupaki"
  )
)

println(orderDescription)
// Jobu Tupaki orders 12 everything bagels
```

Projects
--------

* plugin: The Gradle plugin, with logic to parse string resources and generate formatter methods.
* runtime: The client library, with the types that Gingham requires to work at runtime.
* sample: A sample Android project that demonstrates usage of Gingham.
