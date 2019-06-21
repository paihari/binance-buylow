#!/usr/bin/env bash

mvn clean package
echo "***** CLEAN AND BUILD MAVEN *****"
cp -rf build-config/Dockerfile ./Dockerfile
echo "***** COPY OF DOCKER PROD FILE DONE *****"
cp -rf build-config/.dockerignore ./.dockerignore
echo "***** COPY OF DOCKER PROD IGNORE FILE DONE *****"
cp -rf build-config/env.prod ./env.prod

#Remove all images optional

#docker rm $(docker ps -a -q)

docker build --no-cache -t paihari/binance-buylow:0.0.1-PROD .
echo "***** DOCKER BUILD DONE:  paihari/binance-buylow *****"
cp -rf build-config/Dockerfile ./Dockerfile
echo "***** REVERT BACK TO DOCKER PROD FILE DONE *****"
cp -rf build-config/.dockerignore ./.dockerignore
echo "***** REVERT BACK TO DOCKER PROD IGNORE FILE DONE *****"
rm -rf ./env.prod
echo "********** Done! **********"

