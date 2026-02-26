# Cancerhotspots
A resource for statistically significant mutations in cancer:
[cancerhotspots.org](http://cancerhotspots.org).

## Build
```
docker build -t cancerhotspots -f docker/Dockerfile .
```

## Deploy
```
docker run -p 8081:28080 cancerhotspots
```
## Visit
[localhost:8081](http://localhost:8081/)
