# tf-idf

A term frequency-inverse document frequency (tf-idf) calculator written in Clojure.

To learn more about calculating and the uses of tf-idf visit
[www.tfidf.com](http://www.tfidf.com/). Additionally, this resource provides a clarifying
example.

## Installation

Download the ZIP file [here](https://github.com/mccurdyc/tf-idf/archive/master.zip) or if you are
interested in receiving updates versions of the source code or want to contribute, clone the repository
using the following commands (in the directory where you want the tool to be cloned):

```
$ git clone https://github.com/mccurdyc/tf-idf.git
```

## Usage

To calculate the term frequency-inverse document frequency for a corpus (or directory) use the `-d`
or `--directory` flag with the absolute path to the directory as in the example below.

```
$ java -jar tf-idf.jar -d /path/to/directory
```

## Examples

In this repository, I have provided a `data/` directory with sample text files for which you can
test this tool. The sample directory contains the following files:

```
tf-idf
└───data/
  │   doc1.txt
  │   doc2.txt
  │   doc3.txt
  │   doc4.txt
  │   doc5.txt
```

This is necessary to demonstrate what the output directory structure will look like for a given
input directory. To calculate the term frequency-inverse document frequency for this directory, type the
following into a shell:

```
$ java -jar tf-idf.jar -d data
```

This produces an `output/` directory in the root of the project. Included in this directory will be
a comma-separated values file with the tf-idf values for each file included in the input directory.
The output from the example command above will produce the following `output/` directory:

```
tf-idf
└───data/
│   doc1.txt
│   doc2.txt
│   doc3.txt
│   doc4.txt
│   doc5.txt
│
└───output/
│   doc1-output.csv
│   doc2-output.csv
│   doc3-output.csv
│   doc4-output.csv
│   doc5-output.csv
```

A snippet from an output file `doc1-output.csv` produced, specifically for `doc1.txt`, will look like the following:

```
crust,0.029044825
continental,0.008839729
mantle,0.008208320
billion,0.006945501
earth,0.006314092
oceanic,0.006314092
moon,0.005682683
rocks,0.005051274
km,0.004419865
underlying,0.004419865
...
```

## Development Environment

The tf-idf has been created and run on an Arch Linux 4.8.10-1 workstation with Clojure 1.8.0 and the
OpenJDK 1.8.0_112 version of Java. It was also run on an Arch Linux 4.8.10-1 workstation with Oracle's
Java SE Runtime Environment version 1.8.0_102 as well as OpenJDK 1.7.0_111 version of Java. To further ensure
usability, this tf-idf calculator was run on a Mid-2012 MacBook Pro running MacOS Sierra (Version 10.12.1)
with Oracle's Java SE Runtime Environment version 1.8.0_66. If you are unable to run this tool with your development tools and
your execution environment, then please open a new issue and I will attempt to resolve your concerns.

## License

Distributed under the GNU GENERAL PUBLIC LICENSE version 3.0.

## Problems or Praise?

If you have any problems with downloading or understanding the tool, then please create an issue associated
with this Git repository using the "Issues" link at the top of this site. As the sole contributor to this
repository, I will do everything possible to resolve your issue and provide help where I can to get the
tool working for you in your in your development environment. If you find that this tool helps you
--- as a point of reference or in your tf-idf calculations --- then I also encourage you to "star"
and "watch" this project! Enjoy!
