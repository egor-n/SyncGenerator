# SyncGenerator

[![Build Status](https://travis-ci.org/egor-n/SyncGenerator.svg?branch=master)](https://travis-ci.org/egor-n/SyncGenerator) [![codecov](https://codecov.io/gh/egor-n/SyncGenerator/branch/master/graph/badge.svg?branch=master)](https://codecov.io/gh/egor-n/SyncGenerator?branch=master)

A Java annotation processor that generates synchronized decorator classes
based on [this blog post](http://www.yegor256.com/2017/01/17/synchronized-decorators.html).

## Usage
Applying the `@GenerateSync` to the following interface

```java
@GenerateSync
interface Example {
  void method1(String s);
  Boolean method2(Integer i1, Integer i2) throws IOException;
}
```

will produce the following implementation:

```java
class SyncExample implements Example {
  final Example wrapped;

  public SyncExample(Example wrapped) {
    this.wrapped = wrapped;
  }

  public synchronized void method1(String s) {
    wrapped.method1(s);
  }

  public synchronized Boolean method2(Integer i1, Integer i2) throws IOException {
    return wrapped.method2(i1, i2);
  }
}
```

`@GenerateSync` can also be applied to non-final and non-static classes. Given the following class

```java
@GenerateSync
abstract class Example {
  abstract String s(Boolean b, Integer i);

  final void f() { }

  Double d() throws IOException {
        return null;
    }
}
```

it will generate the synchronized version of it:

```java
class SyncExample extends Example {
  final Example wrapped;

  public SyncExample(Example wrapped) {
    this.wrapped = wrapped;
  }

  synchronized String s(Boolean b, Integer i) {
    return wrapped.s(b, i);
  }

  synchronized Double d() throws IOException {
    return wrapped.d();
  }
}
```

## Download

To use SyncGenerator add these dependencies:

```groovy
compileOnly 'com.github.egor-n:syncgenerator-library:1.0.0'
apt 'com.github.egor-n:syncgenerator-compiler:1.0.0'
```

Snapshots of the development version are available in [Sonatype's snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/).

## TODO

- [ ] add support for inner static classes ([#1](/../../issues/1))
- [ ] figure out how to test this painlessly
