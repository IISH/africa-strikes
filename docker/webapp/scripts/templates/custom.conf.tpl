include "application"

play.evolutions.db.default.autoApply="{{env['APPLY_EVOLUTIONS_DEFAULT'] or "false"}}"

play.crypto.secret = "{{env['PLAY_CRYPTO_SECRET']}}"

db {
    default.driver="{{env['DB_DEFAULT_DRIVER']}}"
    default.url="{{env['DB_DEFAULT_URL']}}"
    default.username="{{env['DB_DEFAULT_USERNAME']}}"
    default.password="{{env['DB_DEFAULT_PASSWORD']}}"
}

{%- if env['LDAP_URL'] %}
ldap {
    url = "{{env['LDAP_URL']}}"
    principal = "{{env['LDAP_PRINCIPAL']}}"
}
{%- endif %}

{%- if env['ADMINS'] %}
admins = [ "{{env['ADMINS']}}" ]
{%- endif %}

articleFilePath = "{{env['ARTICLE_FILE_PATH']}}"

{{env['APP_CUSTOM_CONFIGURATION']}}