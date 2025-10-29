### Backend

Navigate to `src.main.resources`

Run `schema.sql` in your MySQL Workbench to create a database called `dears` on your local machine.

Change `application.properties` to match your local instance of MySQL.

application.properties:

```
spring.datasource.url = jdbc:mysql://localhost:{YOUR_PORT}/project201
spring.datasource.username = {YOUR_USERNAME}
spring.datasource.password = {YOUR_PASSWORD}
```

To start SpringBoot:

```bash
cd backend
./gradleW bootRun
```