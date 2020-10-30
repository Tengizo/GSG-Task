# GSG-Task
Project is built using spring-boot and Angular 


instructions for running

1.project runs with one command. 
 
    mvn spring-boot:run
    
this command will install Node and npm locally, build an angular 
app and copy build  files to resources/static
then starts spring-boot server with url http://localhost:8080.

2.another way to run application is to start api and frontend app separately 
    
    2.1 move to src/main/webapp dir and run "npm install" and "npm start"
    2.2 spring-boot by running main class
    
All the information about users are saved in csvDB/users/users.csv file.
