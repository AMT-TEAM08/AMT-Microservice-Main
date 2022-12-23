# Microservice Main
This is the main application for the AMT Lab, it uses two microservices :
* [DataObject](https://github.com/AMT-TEAM08/Microservice-DataObject)
* [Label Detector](https://github.com/AMT-TEAM08/Microservice-LabelDetector)

## Additionnal information

More general information on the project are available on this [wiki](https://github.com/Nelson-Jnrnd/AMT-Microservice-Main/wiki)

## Introduction
This application is used to detect entities in a picture using two microservices. The first microservice is responsible for storing the image, while the second microservice performs label detection on the image.

## Prerequisites
* Java 17 or higher
* Maven

## Usage
To use this application, you have to compile the project using maven running the following command:
```
mvn clean install
```
Then you can run the application using the following command:
```
java -jar target/microservice-main-1.0.jar <options>...
```
### Options
* -h, --help: Print usage
* -p, --path: Required Path to the file to upload
* -c, --confidence: Confidence threshold for the label detection (default: 0.5)
* -m, --max: Maximum number of labels to return (default: 10)

## Configuration
The following variables can be configured in the source code:

* **DATA_OBJECT_MICROSERVICE_URL**: URL of the microservice responsible for storing the image
* **LABEL_DETECTION_MICROSERVICE_URL**: URL of the microservice responsible for performing label detection on the image
* **LINK_DURAION_IN_MINUTES**: Duration in minutes that the link to the image will be available (default: 5)

## Dependencies
This application depends on the following libraries:

* Apache Commons CLI
* Apache Commons Lang
* Apache HttpClient

## Tests
To run the integration tests you have to launch the two microservices on port 8080 and 8081 respectively. Then run the following command:
```
mvn test
```

## Authors
* De Bleser Dimitri
* Peer Vincent
* Nelson Jeanreneaud
