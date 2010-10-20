 #!/bin/sh

ffmpeg -i "$1" -vn -f wav - | lame --silent -f -V 4 -
