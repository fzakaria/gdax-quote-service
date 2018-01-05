# GDAX Quote Service

The following project is a maven project in order to create a Java service in which to deliver
the best GDAX quotes for a particular BUY/SELL order.
The specifics of the design are outlined in the accompanying PDF file.

## Getting Started

This is a `maven` project so you'll simply have to have maven pre-installed, afterwhich you can run

```mvn clean install```

which will build/test/install the whole mono-repo.
If any tests are failing (they shouldn't be!) you can run the following

```mvn clean install -DskipTests```

## Starting the server

The server is bundled as a _fat jar_ which you can easily execute if you have the JRE already installed.

```java -jar quote-service/target/quote-service-1.0-SNAPSHOT-capsule.jar```

This will start the server on port __8888__

Perform a request!
```
curl -v -H "Content-Type: application/json" -X POST -d '{"action":"BUY", "base_currency":"BTC", "quote_currency":"USD", "amount": "1" }' http://localhost:8888/quote
> {"price":16423.8800000000,"total":1.00000000,"currency":"USD"}
```

You can even use reversed currency pairs that GDAX supports
```
curl -v -H "Content-Type: application/json" -X POST -d '{"action":"BUY", "base_currency":"USD", "quote_currency":"BTC", "amount": "1" }' http://localhost:8888/quote> {"price":16423.8800000000,"total":1.00000000,"currency":"USD"}
> {"price":16432.500000000,"total":0.00006086,"currency":"BTC"}
```

If anything goes wrong it is returned as a JSON error

```
curl -v -H "Content-Type: application/json" -X POST -d '{"action":"BUY", "base_currency":"FAKE", "quote_currency":"BTC", "amount": "1" }' http://localhost:8888/quote
> {"message":"No order book for the provided currencies."}
```
## Philosophy

The code here is structured in a multi-module maven project to help
keep surface area and dependencies better managed.

The following modules are included:

* gdax-client : A retrofit client to interact with GDAX
* swagger-model : The swagger spec file for the quote service
* quote-client : An auto generated swagger client based on the swagger-model
* quote-service : A JAX-RS REST service to ask quotes

The clients and service stubs are generated at _compile_ time.
Although painful to do with Swagger it helps keep the REST service always documented by the swagger file
which can be made readily available to generate clients of other languages.

## Libraries Used

It wouldn't be "Java" unless even this sample project pulled in quite a lot of
dependencies. The same solution could have been done much simpler with much less dependencies
however I wanted to provide something closer to a real-word project that takes care of features 
developers don't handle anymore such as: code generation & json serde.

1. https://github.com/square/retrofit -- easily create interface for REST services
2. https://github.com/swagger-api/swagger-codegen -- maven plugin to generate service stubs & client
3. https://jersey.github.io/ -- Reference JAX-RS REST implementation
4. https://github.com/javaee/grizzly -- Underlying HTTP server
5. https://github.com/FasterXML/jackson -- JSON serde