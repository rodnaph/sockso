#!/bin/sh

faad -q -f 2 -w "$1" | lame --silent -r -f -V 4 - 
