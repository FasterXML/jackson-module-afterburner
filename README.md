Module that will add dynamic bytecode generation for standard Jackson POJO serializers and deserializers, eliminating majority of remaining data binding overhead.
Plugs in using standard Module interface (requiring Jackson 1.8.3 or above).

At this point module should be considered experimental, but it does work for use cases I have tried so far; including jvm-serializers [https://github.com/eishay/jvm-serializers] benchmark (where it helps Jackson data-bind get within 10-15% of "jackson/manual" performance; and 15-20 for Smile).


