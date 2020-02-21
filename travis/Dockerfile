# Use the official Ruby image as a parent image
FROM ruby:latest

# update the repository sources list
# and install dependencies
RUN apt-get update \
    && apt-get -y install maven \
    && apt-get -y install python3-requests \
    # Install Chrome for Selenium
    && curl https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb -o /chrome.deb \ 
    # Install chromedriver for Selenium
    && dpkg -i /chrome.deb || apt-get install -yf \
    && curl https://chromedriver.storage.googleapis.com/2.31/chromedriver_linux64.zip -o /usr/local/bin/chromedriver \
    && chmod +x /usr/local/bin/chromedriver


# install node for lint test dependencies
ENV NODE_VERSION=12.6.0
RUN wget -qO- https://raw.githubusercontent.com/creationix/nvm/v0.34.0/install.sh | bash
ENV NVM_DIR=/root/.nvm
ENV PATH="/root/.nvm/versions/node/v${NODE_VERSION}/bin/:${PATH}"

RUN npm install eslint --save-dev

RUN curl -fsSL https://clis.cloud.ibm.com/install/linux | sh

# Set UTF-8 Locale
ENV LANG C.UTF-8

COPY . .