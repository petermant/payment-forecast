#!/bin/bash
ROOT_DIR=`dirname $0`/..
# have to do a clean as intellij manages test classes in different structure to maven, leading to odd behaviour
${ROOT_DIR}/mvnw clean package
java -jar /home/rpa/payment/target/payment-0.0.1-SNAPSHOT.jar webapp