# FusionAuth SCIM Domain

![semver 2.0.0 compliant](http://img.shields.io/badge/semver-2.0.0-brightgreen.svg?style=flat-square)
![Status: unsupported](https://img.shields.io/badge/status-unsupported-lightgrey.svg?style=flat-square)
![License: Apache 2.0](https://img.shields.io/badge/license-Apache%202.0-blue.svg?style=flat-square)

> [!WARNING]
> **This project is no longer supported by FusionAuth.**
> It is provided for **educational and reference purposes only**. It is not
> maintained, not covered by any FusionAuth support plan or SLA, and not
> intended for production use.

## About

This repository contains Java domain objects for working with
[SCIM 2.0](https://scim.cloud/) (System for Cross-domain Identity Management)
resources. It was originally written to support SCIM implementations in
FusionAuth and is kept public as a reference for anyone learning how SCIM
resources can be modeled in Java.

## What "unsupported" means

- No bug fixes, security patches, or new features will be provided.
- Issues and pull requests are not monitored and may be closed without response.
- No compatibility guarantees. It may not work with current Java versions,
  dependencies, or SCIM clients and servers.
- FusionAuth Support will not assist with questions about this code.
- The library is not tested against, and does not imply compatibility with,
  any current FusionAuth release.

## Alternatives

If you need SCIM in production, don't depend on this repository. Instead:

- Check the [FusionAuth documentation](https://fusionauth.io/docs) for current
  provisioning and identity-sync options, including SCIM availability.
- Use an actively maintained SCIM library for your language, or build against
  the specifications directly:
  [RFC 7642](https://datatracker.ietf.org/doc/html/rfc7642) (concepts),
  [RFC 7643](https://datatracker.ietf.org/doc/html/rfc7643) (schema), and
  [RFC 7644](https://datatracker.ietf.org/doc/html/rfc7644) (protocol).

## Building (for study)

### Building with Savant

If you do not have the Savant build too installed, see below.

```bash
sb int
```

### Install Savant Build 

```bash
mkdir ~/savant
cd ~/savant
wget http://savant.inversoft.org/org/savantbuild/savant-core/1.0.0/savant-1.0.0.tar.gz
tar xvfz savant-1.0.0.tar.gz
ln -s ./savant-1.0.0 current
export PATH=$PATH:~/savant/current/bin/
```

## License

This code is available as open source under the terms of the [Apache v2.0 License](https://opensource.org/licenses/Apache-2.0).
