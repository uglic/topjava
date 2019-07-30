# Test command for curl

## MealRestController

```$bash
#!/bin/bash

# Get meal with id = 100002
curl http://localhost:8081/topjava/rest/meals/100002 -H "Accept: application/json"

# Create new meal
curl -X POST http://localhost:8081/topjava/rest/meals/ -H "Content-Type: application/json" -d '{"dateTime":"2015-05-30T11:30:00","description":"Завтрак с боржоми","calories":504}' 

# Delete meal with id = 100002
curl -X DELETE http://localhost:8081/topjava/rest/meals/100002

# Change meal with id = 100003
curl -X PUT http://localhost:8081/topjava/rest/meals/100003 -H "Content-Type: application/json" -d '{"id":100003, "dateTime":"2015-05-30T11:52:00","description":"Утрик с гербами","calories":554}' 

# Get all meals
curl http://localhost:8081/topjava/rest/meals -H "Accept: application/json"

# Get meals, filtered by dates and times
curl http://localhost:8081/topjava/rest/meals/by?startDate=2015-05-31\&endDate=2015-05-31\&startTime=10%3A20\&endTime=20%3A15 -H "Accept: application/json"

``` 
