#!/bin/bash
set -e

source "/opt/scripts/lib/base_functions.sh"
source "/opt/scripts/lib/functions.sh"
source "/opt/scripts/lib/base_config.sh"
source "/opt/scripts/lib/config.sh"


# generate config files
main() {
    PID_FILE="${APP_HOME}/africa-strikes-${APP_VERSION}/RUNNING_PID"
    if [ -f "$PID_FILE" ]
    then
        rm "$PID_FILE"
    fi
}

main

exit 0