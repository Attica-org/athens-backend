FROM gradle:7.5.1-jdk17
WORKDIR /app
COPY . /app
CMD ["gradle", "bootRun"]