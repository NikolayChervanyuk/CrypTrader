# CrypTrade - Trading platform sim
## Prerequisite
In order to run the project you need to have installed:
1. JVM 21
2. Docker engine
3. node.js along with npm

## How to run

### Frontend
The client application project is located in the frontend folder.
To start the project go inside the folder. Run `npm list --all`. If `ngx-cookie-service` is not included in the list, 
run `npm install ngx-cookie-service`. Finally run `ng build`, then `ng serve`.
The application is listening on port 4200, so you can access it in the browser with url http://localhost:4200

### Backend
The backend application is located in the backend folder.
Go inside the folder. Run Docker engine then run `docker-compose -f compose.yaml up`
Finally, run the Spring application from your IDE.