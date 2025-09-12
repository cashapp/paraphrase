# Change Log

## [Unreleased]
[Unreleased]: https://github.com/cashapp/paraphrase/compare/0.4.0...HEAD

Nothing yet.


## [0.4.1] - 2025-09-12
[0.4.1]: https://github.com/cashapp/paraphrase/releases/tag/0.4.1

New:

- Support for AGP 9.0.0

Changed:

- In-development snapshots are now published to the Central Portal Snapshots repository at https://central.sonatype.com/repository/maven-snapshots/.


## [0.4.0] - 2024-06-11
[0.4.0]: https://github.com/cashapp/paraphrase/releases/tag/0.4.0

New:

- Support for Kotlin 2.0.0

Changed:

- Detect usage of `org.jetbrains.kotlin.plugin.compose` plugin and automatically add
  the `runtime-compose-ui` dependency.


## [0.3.1] - 2023-12-14
[0.3.1]: https://github.com/cashapp/paraphrase/releases/tag/0.3.1

Fixed:

- Include file names in read/parse failures to help with debugging
- Fix crash when processing non-XML resource files

## [0.3.0] - 2023-10-12
[0.3.0]: https://github.com/cashapp/paraphrase/releases/tag/0.3.0

Changed:

- Generate `Number` parameter instead of `Int` for plural and choice arguments
- Generate `Long` parameter along with `Int` overload for ordinal and selectordinal arguments
- Deprecate generated function using choice arguments, which are discouraged in ICU documentation in
  favor of plural and select arguments.

## [0.2.2] - 2023-07-20
[0.2.2]: https://github.com/cashapp/paraphrase/releases/tag/0.2.2

Fixed:

- Fix build cache issue by deleting the generated class when all string resources are removed

## [0.2.1] - 2023-07-19
[0.2.1]: https://github.com/cashapp/paraphrase/releases/tag/0.2.1

Changed:

- Automatically add the `runtime-compose-ui` dependency if buildFeatures.compose is true
- Add missing `)` to `FormattedResource.toString`
- Optimize insertion performance of map arguments

## [0.2.0] - 2023-04-25
[0.2.0]: https://github.com/cashapp/paraphrase/releases/tag/0.2.0

New:

- Add runtime support for Compose UI

## [0.1.2] - 2023-04-17
[0.1.2]: https://github.com/cashapp/paraphrase/releases/tag/0.1.2

Changed:

- Use `androidx.collection.ArrayMap` instead of `android.util.ArrayMap` to hold named arguments.

## [0.1.1] - 2023-04-07
[0.1.1]: https://github.com/cashapp/paraphrase/releases/tag/0.1.1

Fixed:

- Fix crash when processing modules with no merged resources.

## [0.1.0] - 2023-04-06
[0.1.0]: https://github.com/cashapp/paraphrase/releases/tag/0.1.0

Initial release.
