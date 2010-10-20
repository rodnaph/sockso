@echo off

flac -d -c %1 - | lame --silent -
