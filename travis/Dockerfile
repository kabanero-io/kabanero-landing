# Use the official Ruby image as a parent image
FROM ruby:latest

# update the repository sources list
# and install dependencies
RUN apt-get update \
    && apt-get -y install maven \
    && apt-get -y install python3-requests \
    # xvfb enables us to run graphical applications without a display for selenium testing
    && apt-get install -y xvfb \
    # Install Chrome for Selenium
    && curl https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb -o /chrome.deb \ 
    && dpkg -i /chrome.deb || apt-get install -yf 


# install node for lint test dependencies
ENV NODE_VERSION=12.6.0
RUN wget -qO- https://raw.githubusercontent.com/creationix/nvm/v0.34.0/install.sh | bash
ENV NVM_DIR=/root/.nvm
ENV PATH="/root/.nvm/versions/node/v${NODE_VERSION}/bin/:${PATH}"

RUN npm install eslint --save-dev

RUN curl -fsSL https://clis.cloud.ibm.com/install/linux | sh

# Set UTF-8 Locale
ENV LANG C.UTF-8

ENV DISPLAY=:99

COPY . .