# To override, use config.sh


# The configuration files and their templates. Declared in CNF
# Keep the convention: CNF_KEY_$VARIABLE and TPL_KEY_$VARIABLE
readonly CNF_KEYS="CUSTOM SAMPLE"
readonly CNF_SAMPLE="/opt/sample.conf"
readonly TPL_SAMPLE="/opt/scripts/templates/sample.conf.tpl"

readonly CNF_CUSTOM="${APP_HOME}/custom.conf"
readonly TPL_CUSTOM="/opt/scripts/templates/custom.conf.tpl"

readonly IP_ADDRESS=$(get_ipaddress)
#readonly FQDN=$(get_fqdn)
readonly PYTHON_JINJA2="import os;
import sys;
import jinja2;
os.environ['FQDN']='${FQDN}'
os.environ['IP_ADDRESS']='${IP_ADDRESS}'
sys.stdout.write(
    jinja2.Template
        (sys.stdin.read()
    ).render(env=os.environ))"



readonly APP_ARGS="-Dplay.evolutions.db.default.autoApply=${APPLY_EVOLUTIONS_DEFAULT} -J-Xmx1024M -Dconfig.file=${APP_HOME}/custom.conf -Dhttp.port=${APP_PORT}"
readonly APP="${APP_HOME}/africa-strikes-${APP_VERSION}/bin/africa-strikes"