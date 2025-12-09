FROM nstbrowser/browserless:140-202509241200.v2

RUN apt-get update && apt-get install -y \
    openjdk-21-jre \
    tesseract-ocr \
    tesseract-ocr-eng \
    libleptonica-dev \
    libtesseract-dev \
    supervisor \
    && rm -rf /var/lib/apt/lists/*

ENV TESSDATA_PREFIX=/app/configs/tessdata
ENV TOKEN=1f56675b-e14e-4822-8e97-58ac33728252

WORKDIR /app

COPY ContrinueHoverBot-V1.jar /app/ContrinueHoverBot-V1.jar

# как просил — не трогаю эти COPY
COPY configs/tessdata /app/configs/tessdata
COPY configs/tessdata /ContrinueHoverBot/app/configs/tessdata
COPY configs/tessdata /ContrinueHoverBot/configs/tessdata
COPY configs/tessdata /usr/share/tessdata

# supervisor + конфиги
RUN mkdir -p /etc/supervisor/conf.d && \
    { \
      echo '[supervisord]'; \
      echo 'nodaemon=true'; \
      echo '[include]'; \
      echo 'files = /etc/supervisor/conf.d/*.conf'; \
    } > /etc/supervisord.conf && \
    { \
      echo '[program:nst]'; \
      echo 'command=/dockerstartup/startup.sh'; \
      echo 'autostart=true'; \
      echo 'autorestart=true'; \
      echo 'stdout_logfile=/dev/stdout'; \
      echo 'stderr_logfile=/dev/stderr'; \
    } > /etc/supervisor/conf.d/nst.conf && \
    { \
      echo '[program:java]'; \
      printf '%s\n' 'command=java -Djava.awt.headless=false -jar /app/ContrinueHoverBot-V1.jar'; \
      echo 'directory=/app'; \
      echo 'autostart=true'; \
      echo 'autorestart=true'; \
      echo 'stdout_logfile=/dev/stdout'; \
      echo 'stderr_logfile=/dev/stderr'; \
    } > /etc/supervisor/conf.d/java.conf

# supervisord — PID 1, он поднимает и NST, и Java
ENTRYPOINT ["/usr/bin/supervisord", "-c", "/etc/supervisord.conf"]
