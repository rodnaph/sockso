#!/bin/sh

echo "Pulling website..."
cd website
git pull

echo "Pulling master..."
cd ../
git pull
