# Persons
`RestFull API architecture application`

+ ### RestFul API architecture
> \- is a client-server application architecture based on 6 principles.
1. Uniform Interface
2. Stateless
3. Cacheable
4. Client-Server Orientation.
5. Layered System
6. Code on Demand

+ ### Authorization JWT
Authorization in Rest applications occurs through a token - a key by analogy with sessionId in a servlet.

1. Let's check that the security works. Let's try to get users without authorization
   `curl -i http://localhost:8080/all` Result 403
2. Register a user `curl -H "Content-Type: application/json" -X POST -d {"""login""":"""admin""","""password""":"""password"""} "localhost:8080/person/sign-up"`.
3. Let's get a token from this user `curl -i -H "Content-Type: application/json" -X POST -d {"""login""":"""admin""","""password""":"""password"""} "localhost:8080/login"`.
4. Get all users with this token `curl -H "Authorization: Bearer xxx.yyy.zzz" localhost:8080/person/`.