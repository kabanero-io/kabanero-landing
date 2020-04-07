#!/bin/bash
# This script exists to configure the liberty server before starting it up

DEFAULT_CONFIG_DROPIN_PATH='/opt/ol/wlp/usr/servers/defaultServer/configDropins/defaults/'
OVERRIDE_CONFIG_DROPIN_PATH='/opt/ol/wlp/usr/servers/defaultServer/configDropins/overrides/'

SL_CONFIG_FILE='/etc/console_config/social_login/social_login.xml'

KEYSTORE_FILE='/etc/tls/secrets/java.io/landingpage/keystores/keystore.p12'
KEYSTORE_CONFIG_FILE='/etc/console_config/keystore/keystore.xml'

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

# Add keystore and ssl config if the keystore file exists in the container
if [ -f "$KEYSTORE_FILE" ]; then
    mv $KEYSTORE_CONFIG_FILE $OVERRIDE_CONFIG_DROPIN_PATH
fi

# Start the liberty server
exec /opt/ol/wlp/bin/server run "$@"