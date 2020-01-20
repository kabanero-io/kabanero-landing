#!/bin/bash
# This script exists to configure the liberty server before starting it up

DEFAULT_CONFIG_DROPIN_PATH='/opt/ol/wlp/usr/servers/defaultServer/configDropins/defaults/'
SL_CONFIG_FILE='/etc/social_login/social_login.xml'

# Handle the social login feature if the GitHub OAuth secret was mounted
if [ -f /etc/oauth/clientID ] && [ -f /etc/oauth/clientSecret ]; then
    echo "Social Login configuration exists. Setting up Social Login dropin."

    # Mounted in etc/oauth as a secret
    oauthID=$(cat /etc/oauth/clientID)
    oauthSecret=$(cat /etc/oauth/clientSecret)

    sed -i -e "s;{{GITHUB_CLIENT_ID}};$oauthID;g" $SL_CONFIG_FILE
    sed -i -e "s;{{GITHUB_CLIENT_SECRET}};$oauthSecret;g" $SL_CONFIG_FILE

    # The rest are loaded in as env variables
    sed -i -e "s;{{USER_API}};$USER_API;g" $SL_CONFIG_FILE
    sed -i -e "s;{{TOKEN_ENDPOINT}};$TOKEN_ENDPOINT;g" $SL_CONFIG_FILE
    sed -i -e "s;{{AUTHORIZATION_ENDPOINT}};$AUTHORIZATION_ENDPOINT;g" $SL_CONFIG_FILE

    mv $SL_CONFIG_FILE $DEFAULT_CONFIG_DROPIN_PATH
fi

# Start the liberty server
exec /opt/ol/wlp/bin/server run "$@"