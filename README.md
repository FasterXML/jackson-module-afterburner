Module that will add dynamic bytecode generation for standard Jackson POJO serializers and deserializers, eliminating majority of remaining data binding overhead.
Plugs in using standard Module interface (requiring Jackson 1.9.2 or above).

## Status

At this point module should be considered experimental, but it does work for use cases I have tried so far; including jvm-serializers [https://github.com/eishay/jvm-serializers] benchmark (where it helps Jackson data-bind get within 10-15% of "jackson/manual" performance; and 15-20 for Smile).

Current master branch is based on Jackson 2.0.0-SNAPSHOT: older builds (1.9.4) are still available, and source is under "1.x" branch.

## Usage

## Usage

### Maven dependency

To use module on Maven-based projects, use following dependency:

    <dependency>
      <groupId>com.fasterxml.jackson.module</groupId>
      <artifactId>jackson-module-afterburner</artifactId>
      <version>1.9.4</version>
    </dependency>    

(or whatever version is most up-to-date at the moment)

### Registering module

To use the the Module in Jackson, simply register it with the ObjectMapper instance:

    Object mapper = new ObjectMapper()
    mapper.registerModule(new AfterburnerModule());
