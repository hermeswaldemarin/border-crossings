# Getting Started 

# Running Locally

```shell
docker-compose down
mvn clean package 
docker-compose up --build
```

If is the first time you are running the docker-compose, after the docker-compose is up, you need to run this scripts to load data to collection

```shell
wget https://raw.githubusercontent.com/mledoze/countries/master/countries.json
mongoimport --port "27018" --jsonArray --db test --collection country < countries.json
```

* [Accessing Swagger](http://localhost:8080/swagger-ui/index.html)
