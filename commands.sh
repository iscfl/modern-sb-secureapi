#!/bin/bash

openssl genrsa -out refresh-token.pem 2048
openssl pkcs8 -topk8 -in refresh-token.pem  -inform PEM -outform DER -out refresh-token.key -nocrypt
openssl rsa -in refresh-token.key -inform DER -outform DER -pubout -out refresh-token.pub

openssl genrsa -out access-token.pem 2048
openssl pkcs8 -topk8 -in access-token.pem  -inform PEM -outform DER -out access-token.key -nocrypt
openssl rsa -in access-token.key -inform DER -outform DER -pubout -out access-token.pub

