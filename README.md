# Paraphrase

A Gradle plugin that generates type-safe formatters for Android string resources in the ICU message format. It integrates easily with Android Views and Compose UI.

## Usage

### Step 1: Add the Paraphrase Plugin

In the `build.gradle.kts` file of an Android application or library module:

```kotlin
plugins {
  id("app.cash.paraphrase")
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
./gradlew my-module:build
```

Or run the Paraphrase gradle task for the relevant variant:

```shell
./gradlew my-module:generateFormattedResourcesDebug
./gradlew my-module:generateFormattedResourcesRelease
```

That generates a formatted resource function that looks something like this:
```kotlin
/**
 * Describes an order placed at the deli.
 */
public fun order_description(count: Int, name: Any): FormattedResource {
  val arguments = mapOf("count" to count, "name" to name)
  return FormattedResource(
    id = R.string.order_description,
    arguments = arguments,
  )
}
```


### Step 4: Use the Formatted Resources

In an Android View:

```kotlin
import app.cash.paraphrase.getString

val orderDescription = resources.getString(
  FormattedResources.order_description(
    count = 12,
    name = "Jobu Tupaki",
  )
)

// Jobu Tupaki orders 12 everything bagels
```

In Compose UI:

```kotlin
import app.cash.paraphrase.compose.formattedResource

val orderDescription = formattedResource(
  FormattedResources.order_description(
    count = 12,
    name = "Jobu Tupaki",
  ),
)

// Jobu Tupaki orders 12 everything bagels
```

For Compose UI you also need one additional dependency:
```
implementation libs.paraphrase.runtimeComposeUi
```

## Modules

* `plugin`: The Gradle plugin, with logic to parse string resources and generate formatter methods.
* `runtime`: The data types and Android extensions that Paraphrase requires to work at runtime.
* `runtime-compose-ui`: The extensions that Paraphrase requires to work with Compose UI at runtime.
* `sample`: A sample Android project that demonstrates usage of Paraphrase.

## License

    Copyright 2023 Cash App

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
