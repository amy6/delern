# How to contribute to Delern Flashcards

I'm really glad you're reading this, because we need volunteer developers
to help this project to become great!:tada::+1:

## Contributing code in Mobile App

:rocket:Install environment
1. Install Flutter SDK and IDE from
[here](https://flutter.io/docs/get-started/install/)
1. Fork [https://github.com/dasfoo/delern](https://github.com/dasfoo/delern)
into your own GitHub account. If you already have a fork, and are now
installing a development environment on a new machine, make sure you've
updated your fork so that you don't use stale configuration options
from long ago.
1. If you haven't configured your machine with an SSH key that's known
to github, then follow
[GitHub's directions](https://help.github.com/articles/generating-ssh-keys/)
to generate an SSH key.
1. `git clone git@github.com:<your_name_here>/delern.git`
1. `cd delern`
1. `git remote add upstream git@github.com:dasfoo/delern.git`
(So that you fetch from the master repository, not your clone,
when running `git fetch` et al.)
1. Import mobile part of the project. For this in your IDE
import `delern/flutter`

:computer:To start working on a patch
1. `git fetch upstream`
1. `git checkout upstream/master -b <name_of_your_branch>`
1. Hack away
1. Run `flutter analyze` in `delern/flutter` directory to check whether
code complies with style guide
1. Fix style guide warnings
1. `git commit -a -m "<your informative commit message>"`
1. `git push origin <name_of_your_branch>`

Please make sure all your checkins have detailed commit messages explaining
the patch.

:tada:To send us a pull request:
1. `git pull-request` (if you are using [Hub](http://github.com/github/hub/))
or go to [https://github.com/dasfoo/delern](https://github.com/dasfoo/delern) and click
the "Compare & pull request" button

## Reporting Bugs and Suggesting Enhancement

Before creating bug reports, please check
[this list](https://github.com/dasfoo/delern/issues) as you might find out
that you don't need to create one. When you are creating a bug report,
please include as many details as possible.
