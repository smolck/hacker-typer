# hacker-typer

A WIP [re-frame](https://github.com/day8/re-frame) FOSS version of [Typing.io][https://typing.io],
allowing one to type code from any GitHub repo (and hopefully more features which haven't been added/implemented
yet).

## Motivation

I am creating this project to replace any usage of [Typing.io][https://typing.io] for myself, and also to
help others (hopefully) who wish to type out code from GitHub for free.
The requirement of a paid version to type whatever code one wants to on Typing.io is one of the main reasons
I am creating this, in addition to wanting to gain experience with ClojureScript and Re-frame/Reagent ;)

## Contribution

Contributions are welcome, especially code improvements and bug fixes. ATM the code is *really* messy, but I intend on
fixing that soon. See [the TODO file][TODO.org] for a list of todos if you're interested in helping out but not
sure where to start.

## Development

### Running the App

Start a temporary local web server, build the app with the `dev` profile, and serve the app with
hot reload:

```sh
lein dev
```

When `[:app] Build completed` appears in the output, browse to
[http://localhost:8280/](http://localhost:8280/).

[`shadow-cljs`](https://github.com/thheller/shadow-cljs) will automatically push ClojureScript code
changes to your browser on save. To prevent a few common issues, see
[Hot Reload in ClojureScript: Things to avoid](https://code.thheller.com/blog/shadow-cljs/2019/08/25/hot-reload-in-clojurescript.html#things-to-avoid).

Opening the app in your browser starts a
[ClojureScript browser REPL](https://clojurescript.org/reference/repl#using-the-browser-as-an-evaluation-environment),
to which you may now connect.

## Production

Build the app with the `prod` profile:

```sh
lein prod
```

The `resources/public/js/compiled` directory is created, containing the compiled `app.js` and
`manifest.edn` files.

The [`resources/public`](resources/public/) directory contains the complete, production web front
end.
