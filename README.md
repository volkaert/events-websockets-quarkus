# Introduction

The purpose of this demo is to show how to use websockets to publish and subscribe events 
(useful in an Event Driven Architecture - EDA - based system). 

WebSocket is a computer communications protocol, providing full-duplex communication channels over a single TCP connection (see https://en.wikipedia.org/wiki/WebSocket).

In this demo, we use 3 concepts: event (and its `eventCode`), publication, subscription.

- an event is a message sent from a publication to multiple subscriptions. 
An event has an `eventCode` which is the category of the event. 
An `eventCode` is for example `FRAUD_DETECTED` or `TRANSFER_COMPLETED` or `EUR_USD_UPDATED`...
An event has only one `eventCode` but multiple events can be sent with the same `eventCode`.
 
- a publication sends events. 
A publication sends events of a given category (events with a given`eventCode`).
A publisher - a component of the system - can use multiple publications, with one publication for each category of events. 
Publications do not know subscriptions. The ony thing that publications and subscriptions share are the event categories (the `eventCode`).
       
- a subscription receives events. 
A subscription subscribes and receives events of a given category (events with a given`eventCode`).
A subscriber - a component of the system - can use multiple subscriptions, with one subscriptions for each category of events. 
Subscriptions do not know publications. The ony thing that publications and subscriptions share are the event categories (the `eventCode`). 

This demo uses Quarkus (www.quarkus.io) (and not SpringBoot). No special reason for that; it's just because I wanted to play with Quarkus !

# In a nutshell

Build and start the broker:
```
cd broker
../mvnw clean package
java -jar target/events-websockets-quarkus-broker-1.0-SNAPSHOT-runner.jar
```

In a terminal, start a publication for events of category `eventA`:
```
wscat -c localhost:8080/events/eventA/publications
```

In another terminal, start a first subscription for events of category `eventA`:
```
wscat -c localhost:8080/events/eventA/subscriptions
```

In another terminal, start a second subscription for events of category `eventA`:
```
wscat -c localhost:8080/events/eventA/subscriptions
```

In another terminal, start a publication for events of category `eventB`:
```
wscat -c localhost:8080/events/eventB/publications
```

In another terminal, start a first subscription for events of category `eventB`:
```
wscat -c localhost:8080/events/eventB/subscriptions
```

In another terminal, start a second subscription for events of category `eventB`:
```
wscat -c localhost:8080/events/eventB/subscriptions
```
 
In the terminal of the publication for events of category `eventA`, type a message. 
Check the message is displayed in the terminals of the two subscriptions for events of category `eventA`. 

In the terminal of the publication for events of category `eventB`, type a message. 
Check the message is displayed in the terminals of the two subscriptions for events of category `eventB`. 


# Pre-requisites

## Install WSCat

`wscat` is a NodeJS utility used to send and display messages sent/received via websockets (and not regular HTTP REST/JSON API).
  
To install wscat, run:
```
sudo npm install -g wscat
```
To run wscat, run:
```
wscat -c ws://echo.websocket.org
```
> Replace `ws://echo.websocket.org` by the actual endpoint of you websocket


# Broker 

## Build and start

>The broker runs on the port `8080`

### Build and start in dev mode (with hot reload)
```
cd broker
../mvnw compile quarkus:dev
```

### Build and start as a jar
```
cd broker
../mvnw clean package
java -jar target/events-websockets-quarkus-broker-1.0-SNAPSHOT-runner.jar
```

### Test

In a terminal, start a publication for events of category `eventA`:
```
wscat -c localhost:8080/events/eventA/publications
```

In another terminal, start a subscription for events of category `eventA`:
```
wscat -c localhost:8080/events/eventA/subscriptions
```

Type some message in the terminal of the publication and check that the message is diplayed in the terminal of the subscription.  


# Publisher 

## Build and start

>The publisher runs on the port `8081`

### Build and start in dev mode (with hot reload)
```
cd publisher
../mvnw compile quarkus:dev
```

### Build and start as a jar
```
cd publisher
../mvnw clean package
java -jar target/events-websockets-quarkus-publisher-1.0-SNAPSHOT-runner.jar
```

### Test

Publish an event using curl:
```
curl -d '{"eventCode":"eventA", "publicationCode":"pubA1", "payload":"helloA"}' -H "Content-Type: application/json" -X POST http://localhost:8081/events
```

Publish an event using a browser (from the navigation bar of the browser):
```
http://localhost:8081/events/eventA/publications/pubA1?payload=helloA
```

# Subscriber 

## Build and start

>The subscriber runs on the port `8082`

### Build and start in dev mode (with hot reload)
```
cd subscriber
../mvnw compile quarkus:dev
```

### Build and start as a jar
```
cd subscriber
../mvnw clean package
java -jar target/events-websockets-quarkus-subscriber-1.0-SNAPSHOT-runner.jar
```

### Test

Subscribe to an event category using curl:
```
curl -X POST http://localhost:8082/events/eventA/subscriptions/subA1
```

Subscribe to an event category using a browser (from the navigation bar of the browser):
```
http://localhost:8082/events/eventA/subscriptions/subA1
```


