# build server app
mvn clean package

# Run server app
mvn spring-boot:run  or java -jar target/demo-0.0.1-SNAPSHOT.jar

### Access database
http://localhost:8090/h2-console/
jdbc:h2:mem:testdb

### Add data
http://localhost:8090/api/vfi/data

### GET a Sub
http://localhost:8090/api/vfi/0000002178-18-000067

### Run Client Angular App
ng serve

### Build Client Angular App
ng new vfi-app
ng generate service vfiweb

