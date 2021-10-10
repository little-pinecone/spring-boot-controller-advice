# spring-boot-controller-advice

[![keep_growing logo](readme-images/logo_250x60.png)](https://keepgrowing.in/)

This is a demo project to test how global exception handling work in an example Spring Boot project.

![GitHub](https://img.shields.io/github/license/little-pinecone/spring-boot-controller-advice)

## Prerequisites

* JDK 13+
* [Maven](https://maven.apache.org/) (or you can use `mvnw` provided in the project)

## Getting started

First, [clone](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository-from-github/cloning-a-repository)
this repository.

Then, build it locally with:

```bash
mvn clean install
```

Finally, you can run the application with:

```bash
mvn spring-boot:run
```

## Examples

You'll find the Postman collection with example requests in the postman directory. 
The examples contain both correct and incorrect requests to help you work with this project.

Some examples:

![invalid format](readme-images/invalid-format-exception-postman.png)

![json processing](readme-images/json-processing-exception-postman.png)

![method argument not valid](readme-images/method-argument-not-valid-postman.png)

![method argument type mismatch](readme-images/method-argument-type-mismatch-postman.png)

## Built With

* [Spring Boot v2.5+](https://spring.io/projects/spring-boot)
* [Maven](https://maven.apache.org/)
* [Dummy4j](https://daniel-frak.github.io/dummy4j/)