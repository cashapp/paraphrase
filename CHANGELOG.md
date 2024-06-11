# Change Log

## [Unreleased]

Changed:
- Detect usage of `org.jetbrains.kotlin.plugin.compose` plugin and automatically add
  the `runtime-compose-ui` dependency.


## [0.3.1] - 2023-12-14

Fixed:

- Include file names in read/parse failures to help with debugging
- Fix crash when processing non-XML resource files

## [0.3.0] - 2023-10-12

Changed:

- Generate `Number` parameter instead of `Int` for plural and choice arguments
- Generate `Long` parameter along with `Int` overload for ordinal and selectordinal arguments
- Deprecate generated function using choice arguments, which are discouraged in ICU documentation in
  favor of plural and select arguments.

## [0.2.2] - 2023-07-20

Fixed:

- Fix build cache issue by deleting the generated class when all string resources are removed

## [0.2.1] - 2023-07-19

Changed:

- Automatically add the `runtime-compose-ui` dependency if buildFeatures.compose is true
- Add missing `)` to `FormattedResource.toString`
- Optimize insertion performance of map arguments

## [0.2.0] - 2023-04-25

New:

- Add runtime support for Compose UI

## [0.1.2] - 2023-04-17

Changed:

- Use `androidx.collection.ArrayMap` instead of `android.util.ArrayMap` to hold named arguments.

## [0.1.1] - 2023-04-07

Fixed:

- Fix crash when processing modules with no merged resources.

## [0.1.0] - 2023-04-06

Initial release.


[Unreleased]: https://github.com/cashapp/paraphrase/compare/0.3.1...HEAD
[0.3.1]: https://github.com/cashapp/paraphrase/releases/tag/0.3.1
[0.3.0]: https://github.com/cashapp/paraphrase/releases/tag/0.3.0
[0.2.2]: https://github.com/cashapp/paraphrase/releases/tag/0.2.2
[0.2.1]: https://github.com/cashapp/paraphrase/releases/tag/0.2.1
[0.2.0]: https://github.com/cashapp/paraphrase/releases/tag/0.2.0
[0.1.2]: https://github.com/cashapp/paraphrase/releases/tag/0.1.2
[0.1.1]: https://github.com/cashapp/paraphrase/releases/tag/0.1.1
[0.1.0]: https://github.com/cashapp/paraphrase/releases/tag/0.1.0
