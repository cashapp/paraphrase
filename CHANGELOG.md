# Change Log

## [Unreleased]

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


[Unreleased]: https://github.com/cashapp/paraphrase/compare/0.2.1...HEAD
[0.2.1]: https://github.com/cashapp/paraphrase/releases/tag/0.2.1
[0.2.0]: https://github.com/cashapp/paraphrase/releases/tag/0.2.0
[0.1.2]: https://github.com/cashapp/paraphrase/releases/tag/0.1.2
[0.1.1]: https://github.com/cashapp/paraphrase/releases/tag/0.1.1
[0.1.0]: https://github.com/cashapp/paraphrase/releases/tag/0.1.0
