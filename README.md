Gingham
=======

A Gradle plugin that generates type checked formatters for patterned Android string resources.

Usage
-----

### Step 1: Add the Gingham Plugin

In the `build.gradle.kts` file of an Android application or library module:

```kotlin
plugins {
  id("app.cash.gingham")
}
```

### Step 2: Add an ICU String Resource

In the `strings.xml` file within the module:

```xml

<resources>
  <!-- Describes an order placed at the deli. -->
  <string name="order_description">
    {count, plural,
      =0 {{name} does not order any bagels}
      =1 {{name} orders an everything bagel}
      other {{name} orders # everything bagels}
    }
  </string>
</resources>
```

For more information on the ICU message format, see the [ICU docs](https://unicode-org.github.io/icu/userguide/format_parse/messages).

### Step 3: Generate the Formatted Resources

Build the module:

```shell
./gradlew my-app:build
```

Or run the Gingham gradle task for the relevant build flavor:

```shell
./gradlew my-app:generateFormattedResourcesDebug
./gradlew my-app:generateFormattedResourcesRelease
```

That generates a formatted resource method that looks like this:
```kotlin
/**
 * Describes an order placed at the deli.
 */
public fun FormattedResources.order_description(count: Int, name: Any): FormattedResource {
  val namedArgs = mapOf("count" to count, "name" to name)
  return IcuNamedArgFormattedResource(
    id = R.string.order_description,
    namedArgs = namedArgs
  )
}
```


### Step 4: Use the Formatted Resources

In your view modules:

```kotlin
import app.cash.gingham.getString

val orderDescription = resources.getString(
  FormattedResources.order_description(
    count = 12,
    name = "Jobu Tupaki"
  )
)

println(orderDescription)
// Jobu Tupaki orders 12 everything bagels
```

Or in your presenter modules:

```kotlin
val orderDescription = stringManager.getString(
  FormattedResources.order_description(
    count = 12,
    name = "Jobu Tupaki"
  )
)

println(orderDescription)
// Jobu Tupaki orders 12 everything bagels
```

Modules
-------

* `plugin`: The Gradle plugin, with logic to parse string resources and generate formatter methods.
* `runtime`: The client library, with the types that Gingham requires to work at runtime.
* `sample`: A sample Android project that demonstrates usage of Gingham.
