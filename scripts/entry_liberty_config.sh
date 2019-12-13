#!/bin/bash
# This script exists to configure the liberty server.xml before starting up the server

DEFAULT_CONFIG_DROPIN_PATH='/opt/ol/wlp/usr/servers/defaultServer/configDropins/defaults/'

# Handle the social login feature if the GitHub OAuth credentials exist
if [[ -z "${GITHUB_OAUTH_CLIENT_ID}" ]]; then
    echo "Social Login OAuth is configured"
    mv
fi
env
#echo "${server.config.dir}/configDropins/"
# Start the liberty server

/opt/ol/wlp/bin/server run defaultServer