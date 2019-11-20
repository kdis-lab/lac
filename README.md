# LAC: Library for Associative Classification

### What is LAC?

LAC is the first Associative Classification library, aiming at covering many well-known algorithms for AC. This tool is specifically designed to be used on AC, unlike other tools which are more generic. LAC has not only algorithms, but this library also provides different reports to quantify quality of the solutions. In this sense, interpretability measures have been added to gauge the interpretability of the classifiers. Likewise, many accuracy measures are also been included to quantify how accurate are the predictions. Finall, LAC also provides a framework to automate experimental studies, supporting both sequential and parallel executions.


## FAQ

* [How to download LAC?](https://github.com/kdis-lab/lac/wiki/Downloading-LAC)
* [How to get/build LAC?](https://github.com/kdis-lab/lac/wiki/Building-LAC)
* [What algorithms are included in LAC?](https://github.com/kdis-lab/lac/wiki/Algorithms-included-in-LAC)
* [What types of datasets are supported by LAC?](https://github.com/kdis-lab/lac/wiki/Type-of-datasets-supported-by-LAC)
* [What changes are in the new version of LAC?](https://github.com/kdis-lab/lac/blob/main/CHANGELOG.md)
* [How can I run LAC? Could I see some examples?](https://github.com/kdis-lab/lac/wiki/Examples.-Running-LAC)

Thinking in developing to add new algorithms or to fix bugs:

* [What are the structure of classes in LAC?](https://github.com/kdis-lab/lac/wiki/Structure-of-classes-in-LAC)
* [How to run the suite of tests?](https://github.com/kdis-lab/lac/wiki/Running-tests)


If this FAQ is not enough, please see:

* Manual: there is a user/developer manual available at the repository as `doc/manual.pdf`. [Direct link](https://github.com/kdis-lab/lac/blob/v0.2.0/doc/manual.pdf)
* Javadoc: current source code is also documented by means of [javadoc](https://docs.oracle.com/javase/8/docs/technotes/tools/windows/javadoc.html). Documentation is also at the repository as `doc/javadoc`. [Direct Link](https://github.com/kdis-lab/lac/blob/v0.2.0/doc/javadoc)


## Contributing

Everyone interacting in LAC codebases, issue trackers, and wiki is expected to follow the [Covenant](https://www.contributor-covenant.org/version/2/0/code_of_conduct) code of conduct.

1. [Fork it](http://github.com/kdis-lab/lac/fork)
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Make sure that all the tests are green, and tests for new features have been added. (`mvn test`)
4. Commit your changes (`git commit -am 'Add some feature'`)
5. Push to the branch (`git push origin my-new-feature`)
6. Create new [Pull Request](https://help.github.com/en/desktop/contributing-to-projects/creating-a-pull-request)
7. Don't forget to add tests or to modify it. In each Pull Request tests will be automatically run using [Github Actions](https://github.com/features/actions).

## Reporting bugs

LAC makes use of [issues of Github](https://github.com/kdis-lab/lac/issues) to keep track of bugs, enhancements, or other requests. Please, bore in mind that when reporting issues details or steps on how to reproduce the bug has to be added to facilitate reproducibility. Follow the github manual on [how to create a issue](https://help.github.com/en/github/managing-your-work-on-github/creating-an-issue).

## Authors - Citation

This library is currently in a reviewing process in the prestigious Knowledge-Based Systems journal.

This work has been performed by F. Padillo, JM. Luna and S. Ventura.

## License

LAC is released under [GPLv3](https://opensource.org/licenses/GPL-3.0)

