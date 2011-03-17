#!/bin/sh

echo "Pushing website..."
cd website
git push origin master

echo "Pushing master..."
cd ../
git push origin master
