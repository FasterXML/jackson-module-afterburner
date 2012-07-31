Module that will add dynamic bytecode generation for standard Jackson POJO serializers and deserializers, eliminating majority of remaining data binding overhead.

Plugs in using standard Module interface (requiring Jackson 2.0.0 or above).

For Javadocs, Download, see: [Wiki](jackson-module-afterburner/wiki).


## Status

At this point module should be considered experimental, but it does work for use cases I have tried so far; including jvm-serializers [https://github.com/eishay/jvm-serializers] benchmark (where it helps Jackson data-bind get within 10-15% of "jackson/manual" performance; and 15-20 for Smile).

Current master branch works with Jackson 2.0.x:, and this is the actively developed version.
Older builds (1.9.4) are still available, and source is under "1.x" branch, but no further development is planned for this version.

## Usage

### Maven dependency

To use module on Maven-based projects, use following dependency:

    <dependency>
      <groupId>com.fasterxml.jackson.module</groupId>
      <artifactId>jackson-module-afterburner</artifactId>
      <version>2.0.5</version>
    </dependency>    

(or whatever version is most up-to-date at the moment)

### Non-Maven

For non-Maven use cases, you download jars from [Central Maven repository](http://repo1.maven.org/maven2/com/fasterxml/jackson/module/jackson-module-afterburner/) or [Download page](jackson-databind/wiki/JacksonDownload).

Databind jar is also a functional OSGi bundle, with proper import/export declarations, so it can be use on OSGi container as is.

### Registering module

To use the the Module in Jackson, simply register it with the ObjectMapper instance:

    Object mapper = new ObjectMapper()
    mapper.registerModule(new AfterburnerModule());

after which you just do data-binding as usual:

    Value val = mapper.readValue(jsonSource, Value.class);
    mapper.writeValue(new File("result.json"), val);

### What is optimized?

Following things are optimized:

* For serialization (POJOs to JSON):
 * Mutators for "setting" value (field access, calling setter method) are inlined using generated code instead of reflection
 * Serializers for small number of 'primitive' types (`int`, `long`, String) are replaced with direct calls, instead of getting delegated to `JsonSerializer`s
* For deserialization (JSON to POJOs)
 * Calls to default (no-argument) constructors are byte-generated instead of using reflection
 * Accessors for "getting" values  (field access, calling setter method) are inlined using generated code instead of reflection
 * Deserializers for small number of 'primitive' types (`int`, `long`, String) are replaced with direct calls, instead of getting delegated to `JsonDeserializer`s

### More

Check out [Wiki](jackson-module-afterburner/wiki).
