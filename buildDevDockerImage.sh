#!/usr/bin/env bash

mvn clean package
cp -rf build-config/Dockerfile.dev ./Dockerfile
echo "***** COPY OF DOCKER DEV FILE DONE *****"
cp -rf build-config/.dockerignore.dev ./.dockerignore
echo "***** COPY OF DOCKER DEV IGNORE FILE DONE *****"
cp -rf build-config/env.dev ./env.dev

#Remove all images optional

#docker rm $(docker ps -a -q)


docker build --no-cache -t paihari/binance-buylow:latest .
echo "***** DOCKER BUILD DONE:  paihari/binance-buylow *****"
cp -rf build-config/Dockerfile ./Dockerfile
echo "***** REVERT BACK TO DOCKER PROD FILE DONE *****"
cp -rf build-config/.dockerignore ./.dockerignore
echo "***** REVERT BACK TO DOCKER PROD IGNORE FILE DONE *****"
rm -rf ./env.dev
echo "********** Done! **********"

