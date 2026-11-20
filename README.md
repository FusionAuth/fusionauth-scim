## FusionAuth SCIM Domain ![semver 2.0.0 compliant](http://img.shields.io/badge/semver-2.0.0-brightgreen.svg?style=flat-square)

Support for SCIM implementations

## Building

The build uses Gradle and Java 25 from [mise](https://mise.jdx.dev/).

```bash
mise install
gradle build
```

To install the jar into your local Maven repository (`~/.m2`):

```bash
gradle publishToMavenLocal
```

Dependencies come from Maven Central. To use a private repository instead (CodeArtifact), set the `codeArtifactUrl`
Gradle property (for example in `~/.gradle/gradle.properties`) or the `CODEARTIFACT_URL` environment variable; the auth
token is fetched automatically (see `gradle/codeArtifactTokenFetcher.groovy`).

## License

This code is available as open source under the terms of the [Apache v2.0 License](https://opensource.org/licenses/Apache-2.0).
