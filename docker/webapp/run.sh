#!/bin/bash
set -e

$DEBUG_COMMAND

source "/opt/scripts/lib/base_functions.sh"
source "/opt/scripts/lib/functions.sh"
source "/opt/scripts/lib/base_config.sh"
source "/opt/scripts/lib/config.sh"


run_scripts() {
    local run_script_dir="/opt/scripts/${1}"
    for script in ${run_script_dir}/*.sh
    do
        if [ -f ${script} -a -x ${script} ]
        then
            ${script}
        fi
    done
}


# pre_scripts
# Configuration of the container.
pre_scripts() {
    run_scripts "pre"
}

# post_scripts
# To run after the appplication starts.
post_scripts() {
    run_scripts "post"
}

main() {
    pre_scripts

    opt=$1
    case "$opt" in
        "" | start)
            echo "Start ${APP} ${APP_ARGS}"
            cd "${APP_HOME}/africa-strikes-${APP_VERSION}"
            exec /usr/local/bin/gosu $APP_USER $APP $APP_ARGS
        ;;
        *)
            exec $@
        ;;
    esac

    post_scripts
}


main "$@"