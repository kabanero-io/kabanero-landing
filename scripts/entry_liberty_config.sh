#!/bin/bash
# This script exists to configure the liberty server before starting it up

DEFAULT_CONFIG_DROPIN_PATH='/opt/ol/wlp/usr/servers/defaultServer/configDropins/defaults/'

# Handle the social login feature if the GitHub OAuth credentials exist

if [ -f /etc/consoleoauthid ] && [ -f /etc/consoleoauthsecret ]; then
    echo "Social Login is configured"
    SOCIAL_LOGIN_CONFIG_FILE="/opt/ol/wlp/etc/social_login.xml"

    oauthid=$(cat /etc/consoleoauthid)
    oauthsecret=$(cat /etc/consoleoauthsecret)

    sed -i -e "s/{{GITHUB_CLIENT_ID}}/$oauthid/g" $SOCIAL_LOGIN_CONFIG_FILE
    sed -i -e "s/{{GITHUB_CLIENT_SECRET}}/$oauthsecret/g" $SOCIAL_LOGIN_CONFIG_FILE

    mv $SOCIAL_LOGIN_CONFIG_FILE $DEFAULT_CONFIG_DROPIN_PATH
fi
cat /opt/ol/wlp/usr/servers/defaultServer/server.xml
# Start the liberty server
exec /opt/ol/wlp/bin/server run "$@"