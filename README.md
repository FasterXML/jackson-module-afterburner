Module that will add dynamic bytecode generation for standard Jackson POJO serializers and deserializers, eliminating majority of remaining data binding overhead.
Plugs in using standard Module interface (requiring Jackson 1.8.3 or above).

## Status

At this point module should be considered experimental, but it does work for use cases I have tried so far; including jvm-serializers [https://github.com/eishay/jvm-serializers] benchmark (where it helps Jackson data-bind get within 10-15% of "jackson/manual" performance; and 15-20 for Smile).

## Usage

## Usage

### Maven dependency

To use module on Maven-based projects, use following dependency:

    <dependency>
      <groupId>com.fasterxml.jackson</groupId>
      <artifactId>jackson-module-afterburner</artifactId>
      <version>0.7.0</version>
    </dependency>    

(or whatever version is most up-to-date at the moment)

### Registering module

To use the the Module in Jackson, simply register it with the ObjectMapper instance:

    Object mapper = new ObjectMapper()
    mapper.registerModule(new AfterburnerModule());
