FROM ruby:2.6.5 as builder

# Install Java
RUN curl -L -o /tmp/jdk.tar.gz https://github.com/AdoptOpenJDK/openjdk8-binaries/releases/download/jdk8u232-b09_openj9-0.17.0/OpenJDK8U-jdk_x64_linux_openj9_8u232b09_openj9-0.17.0.tar.gz \
    && mkdir -p /opt/java/openjdk \
    && tar -xzf /tmp/jdk.tar.gz --strip-components=1 -C /opt/java/openjdk \
    && chown -R root:root /opt/java \
    && rm /tmp/jdk.tar.gz
    
ENV JAVA_HOME /opt/java/openjdk

# Install Node
ENV NODE_VERSION 10.15.3
RUN curl -fsSLO --compressed "https://nodejs.org/dist/v$NODE_VERSION/node-v$NODE_VERSION-linux-x64.tar.xz" \
    && tar -xf "node-v$NODE_VERSION-linux-x64.tar.xz" -C /opt/ \
    && rm "node-v$NODE_VERSION-linux-x64.tar.xz"

# Install Apache Maven
ENV MAVEN_VERSION 3.6.2
RUN curl -o /tmp/maven.tar.gz https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
    && tar -xzf /tmp/maven.tar.gz \
    && mv apache-maven-${MAVEN_VERSION} /opt/ \
    && rm /tmp/maven.tar.gz

ENV PATH=/opt/java/openjdk/bin:/opt/node-v$NODE_VERSION-linux-x64/bin/:/opt/apache-maven-${MAVEN_VERSION}/bin:$PATH

# Set UTF-8 Locale
ENV LANG C.UTF-8

COPY ./scripts/build_gem_dependencies.sh /app/scripts/build_gem_dependencies.sh
COPY Gemfile* /app/
COPY gems /app/gems

WORKDIR /app

RUN bash ./scripts/build_gem_dependencies.sh

COPY . /app

ARG DOCS_GIT_URL
ARG DOCS_GIT_REVISION
ARG GUIDES_GIT_URL
ARG GUIDES_GIT_REVISION

ENV JEKYLL_ENV "production"
ENV DOCS_GIT_URL $DOCS_GIT_URL
ENV DOCS_GIT_REVISION $DOCS_GIT_REVISION
ENV GUIDES_GIT_URL $GUIDES_GIT_URL
ENV GUIDES_GIT_REVISION $GUIDES_GIT_REVISION

RUN bash ./scripts/build_jekyll_maven.sh

# ------------------------------------------------------------------------------------------------

FROM openliberty/open-liberty:19.0.0.12-kernel-java8-openj9-ubi

LABEL name="kabanero-console" \
      summary="Kabanero Console" \
      description="Kabanero Console"

# Set git.revision label
ARG GIT_REVISION=0
LABEL "git.revision"="$GIT_REVISION"

# Set product version label and env
ARG PRODUCT_VERSION=0
LABEL "product.version"="$PRODUCT_VERSION"
ENV PRODUCT_VERSION="$PRODUCT_VERSION"

USER root

COPY src/main/wlp/etc/social_login.xml /etc/console_config/social_login/
COPY src/main/wlp/etc/keystore.xml /etc/console_config/keystore/

# Symlink servers directory for easier mounts.
# Change /etc/console_config permissions so that group 0 can access it
RUN ln -s /opt/ol/wlp/usr/servers /servers && \
    chgrp -R 0 /etc/console_config && \
    chmod -R g=u /etc/console_config

USER 1001

COPY --from=builder /app/target/liberty/wlp/usr/servers /servers
COPY LICENSE /licenses
COPY scripts/entry_liberty_config.sh /scripts/

# Run the server script and start the defaultServer by default.
ENTRYPOINT ["/scripts/entry_liberty_config.sh"]
CMD ["defaultServer"]
