# Getting Started 

to run unit tests the docker deamon need to be runing and the port 27017 should not to be in use.

# Running Locally

You need to be with mongodb running on localhost 27017
You need to be with redis running on localhost 6379

Run this scripts to load data to collection

```shell
wget https://raw.githubusercontent.com/mledoze/countries/master/countries.json
mongoimport --jsonArray --db test --collection country < countries.json
```

* [Accessing Swagger](http://localhost:8080/swagger-ui/index.html)
