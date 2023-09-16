# `teodorlu.bb-install` - an experiment on top of bbin.

⚠️ WARNING: THIS CODE IS NOT RECOMMENDED FOR PRODUCTION USE. ⚠

You're looking at a work-in-process experiment on top of `babashka/bbin`.
If you just want to install babashka script, please see the official bbin repository:

https://github.com/babashka/bbin

I track my progress in [teodor.org](teodor.org).

⚠️ The rest of this README is copied verbatim from the original README.
With my code changes, this README may give incorrect instructions. ⚠

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [Docs](#docs)
- [CLI](#cli)
- [Contributing](#contributing)
- [License](#license)

## Installation

### Installing for local development

Note: this requies a version of the original bbin (https://github.com/babashka/bbin) to bootstrap.

From this folder, run:

``` shell
bbin install . --as bb-install --main-opts '["-m" "teodorlu.bb-install.cli/-main"]'
```

## Usage

```
# Install a script from a qualified lib name
$ bbin install io.github.babashka/neil
$ bbin install io.github.rads/watch --latest-sha
$ bbin install org.babashka/http-server --mvn/version 0.1.11

# Install an auto-generated CLI from a namespace of functions
$ bbin install io.github.borkdude/quickblog --tool --ns-default quickblog.api

# Install a script from a URL
$ bbin install https://gist.githubusercontent.com/rads/da8ecbce63fe305f3520637810ff9506/raw/25e47ce2fb5f9a7f9d12a20423e801b64c20e787/portal.clj
$ bbin install https://github.com/babashka/http-server/releases/download/v0.1.11/http-server.jar

# Install a script from a Git repo
$ bbin install https://gist.github.com/1d7670142f8117fa78d7db40a9d6ee80.git
$ bbin install git@gist.github.com:1d7670142f8117fa78d7db40a9d6ee80.git

# Install a script from a local file
$ bbin install foo.clj
$ bbin install http-server.jar

# Install a script from a local root (with no lib name)
$ git clone https://github.com/babashka/bbin.git ~/src/bbin
$ bbin install ~/src/bbin --as bbin-dev

# Install a script from a local root (with lib name)
$ bbin install io.github.babashka/bbin --local/root ~/src/bbin --as bbin-dev

# Remove a script
$ bbin uninstall watch

# Show installed scripts
$ bbin ls

# Show the bin path
$ bbin bin
```

## Docs

- [CLI Docs](#cli)
- [FAQ](docs/faq.md)
- [Design Docs](docs/design.md)
- [Community Scripts and Projects](https://github.com/babashka/bbin/wiki/Scripts-and-Projects)
- [Auto-Completion](docs/auto-completion.md)

## CLI

- [`bbin install [script]`](#bbin-install-script)
- [`bbin uninstall [script]`](#bbin-uninstall-script)
- [`bbin ls`](#bbin-ls)
- [`bbin bin`](#bbin-bin)
- [`bbin version`](#bbin-version)
- [`bbin help`](#bbin-help)

---

### `bbin install [script]`

**Install a script**

- By default, scripts will be installed to `~/.babashka/bbin/bin`
    - If `$BABASHKA_BBIN_DIR` is set, then use `$BABASHKA_BBIN_DIR` (explicit override)
    - If `$XDG_DATA_HOME` is set, then use `$XDG_DATA_HOME/.babashka/bbin/bin` (Freedesktop conventions)
- Each bin script is a self-contained shell script that fetches deps and invokes `bb` with the correct arguments.
- The bin scripts can be configured using the CLI options or the `:bbin/bin` key in `bb.edn`
- [See the FAQ for additional info on setting up your code to work with bbin](docs/faq.md#how-do-i-get-my-software-onto-bbin)

**Example `bb.edn` Config:**

```clojure
{:bbin/bin {neil {:main-opts ["-f" "neil"]}}}
```

**Supported Options:**

*Note:* `bbin` will throw an error if conflicting options are provided, such as using both `--git/sha` and `--mvn/version` at the same time.

If no `--git/tag` or `--git/sha` is provided, the latest tag from the Git repo will be used. If no tags exist, the latest SHA will be used.

- `--as`
    - The name of the script to be saved in the `bbin bin` path
- `--git/sha`
    - The SHA for a Git repo
- `--git/tag`
    - The tag for a Git repo
- `--git/url`
    - The URL for a Git repo
- `--latest-sha`
    - If provided, find the latest SHA from the Git repo
- `--local/root`
    - The path of a local directory containing a `deps.edn` file
- `--main-opts`
    - The provided options (EDN format) will be passed to the `bb` command-line when the installed script is run
    - By default, `--main-opts` will be set to `["-m" ...]`, inferring the main function from the lib name
    - For example, if you provide a lib name like `io.github.rads/watch`, `bbin` will infer `rads.watch/-main`
    - Project authors can provide a default in the `:bbin/bin` key in `bb.edn`
- `--mvn/version`
    - The version of a Maven dependency
- `--ns-default`
    - The namespace to use to find functions (tool mode only)
    - Project authors can provide a default in the `:bbin/bin` key in `bb.edn`
- `--tool`
    - If this option is provided, the script will be installed using **tool mode**
    - When enabled, the installed script acts as an entry point for functions in a namespace, similar to `clj -T`
    - If no function is provided, the installed script will infer a help message based on the function docstrings
---

### `bbin uninstall [script]`

**Remove a script**

---

### `bbin ls`

**List installed scripts**

---

### `bbin bin`

**Display bbin bin folder**

- The default folder is `~/.babashka/bbin/bin`

---

### `bbin version`

**Display bbin version**

---

### `bbin help`

**Display bbin help**

---

## Contributing

If you'd like to contribute to `bbin`, you're welcome to create [issues for ideas, feature requests, and bug reports](https://github.com/babashka/bbin/issues).

## License

`bbin` is released under the [MIT License](LICENSE).
