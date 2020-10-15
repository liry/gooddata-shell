# GoodData Shell

![CI](https://github.com/liry/gooddata-shell/workflows/CI/badge.svg?branch=master&event=push)

The *GoodData Shell* is a command line client for interacting with the [GoodData Platform](http://www.gooddata.com/).
It is built on the [GoodData Java SDK](https://github.com/gooddata/gooddata-java) and the [Spring Shell](https://docs.spring.io/spring-shell/docs/current/reference/htmlsingle/).

## Features

* tab completions
* command history
* colorized output
* script execution (run with `--cmdfile file.txt` command line argument or use `script` command)

## Usage

1. [Download](https://github.com/liry/gooddata-shell/releases)
2. Run `java -jar gooddata-shell-1.0-SNAPSHOT.jar`
3. Type `help` to get started

## Develop

1. `mvn verify`
2. `mvn exec:java`

### Update Driver
1. `mvn -f driver/pom.xml clean package`
1. commit new jar file
