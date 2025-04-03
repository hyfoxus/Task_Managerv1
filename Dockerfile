FROM ubuntu:latest
LABEL authors="gnemirko"

ENTRYPOINT ["top", "-b"]