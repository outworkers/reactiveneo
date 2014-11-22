# reactiveneo [![Build Status](https://travis-ci.org/websudos/reactiveneo.svg?branch=develop)](https://travis-ci.org/websudos/reactiveneo)

Reactive type-safe Scala DSL for Neo4j

<a id="contributing">Contributing to ReactiveNeo</a>
==============================================
<a href="#table-of-contents">Back to top</a>

Contributions are most welcome!

1. If you don't have direct write access(e.g. you are not from Websudos), fork the repository first.
2. Create a feature branch where all the changes will be made.
3. Do your awesome stuff!
4. Create a release branch according to the GitFlow guidelines.
5. Create a Pull Request from that release branch.
6. Wait for us to merge!(P.S.: We like to think we're really quick at that)


<a id="git-flow">Using GitFlow</a>
==================================
<a href="#table-of-contents">Back to top</a>

To contribute, simply submit a "Pull request" via GitHub.

We use GitFlow as a branching model and SemVer for versioning.

- When you submit a "Pull request" we require all changes to be squashed.
- We never merge more than one commit at a time. All the n commits on your feature branch must be squashed.
- We won't look at the pull request until Travis CI says the tests pass, make sure tests go well.

<a id="style-guidelines">Scala Style Guidelines</a>
===================================================
<a href="#table-of-contents">Back to top</a>

In spirit, we follow the [Twitter Scala Style Guidelines](http://twitter.github.io/effectivescala/).
We will reject your pull request if it doesn't meet code standards, but we'll happily give you a hand to get it right. Morpheus is even using ScalaStyle to 
build, which means your build will also fail if your code doesn't comply with the style rules.

Some of the things that will make us seriously frown:

- Blocking when you don't have to. It just makes our eyes hurt when we see useless blocking.
- Testing should be thread safe and fully async, use ```ParallelTestExecution``` if you want to show off.
- Writing tests should use the pre-existing tools.
- Use the common patterns you already see here, we've done a lot of work to make it easy.
- Don't randomly import stuff. We are very big on alphabetized clean imports.
- Morpheus uses ScalaStyle during Travis CI runs to guarantee you are complying with our guidelines. Since breaking the rules will result in a failed build, 
please take the time to read through the guidelines beforehand.


