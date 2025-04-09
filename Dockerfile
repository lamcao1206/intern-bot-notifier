FROM ubuntu:20.04

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y \
    wget \
    curl \
    openjdk-21-jdk \
    libnss3 \
    libxss1 \
    libappindicator3-1 \
    libgdk-pixbuf2.0-0 \
    libdbus-glib-1-2 \
    libnspr4 \
    libxtst6 \
    xdg-utils \
    fonts-liberation \
    libasound2 \
    libatk-bridge2.0-0 \
    libatk1.0-0 \
    libatspi2.0-0 \
    libc6 \
    libcairo2 \
    libcups2 \
    libcurl3-gnutls \
    libexpat1 \
    libgbm1 \
    libglib2.0-0 \
    libgtk-3-0 \
    libudev1 \
    libvulkan1 \
    libx11-6 \
    libxcb1 \
    libxcomposite1 \
    libxdamage1 \
    libxext6 \
    libxfixes3 \
    libxkbcommon0 \
    libxrandr2 \
    && wget -q https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb \
    && apt-get install -y ./google-chrome-stable_current_amd64.deb \
    && rm -f google-chrome-stable_current_amd64.deb \
    && rm -rf /var/lib/apt/lists/*

ENV CHROME_BIN=/usr/bin/google-chrome-stable
ENV CHROME_DRIVER=/usr/lib/chromium-browser/chromedriver
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

WORKDIR /app

COPY target/intern-notifier-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/telegram_credentials.json /app/telegram_credentials.json

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 8080