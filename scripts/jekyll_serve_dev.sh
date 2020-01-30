#!/bin/bash

CONTENT_DIR="src/main/content"

npm install

# Copy carbon icons so jekyll will include them
CARBON_ICONS_DIR="$CONTENT_DIR/img/carbon-icons/"
mkdir $CARBON_ICONS_DIR
cp -t "$CONTENT_DIR/img/carbon-icons/" ./node_modules/carbon-icons/dist/carbon-icons.svg ./node_modules/carbon-icons/dist/svg

bundle exec jekyll s --host 0.0.0.0 --source src/main/content --config src/main/content/_config.yml,src/main/content/_dev_config.yml --drafts