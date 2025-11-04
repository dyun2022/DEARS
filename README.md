### Backend

Navigate to `src.main.resources`

Run `schema.sql` in your MySQL Workbench to create a database called `dears` on your local machine.

Change `application.properties` to match your local instance of MySQL.

application.properties:

```
spring.datasource.url = jdbc:mysql://localhost:{YOUR_PORT}/dears
spring.datasource.username = {YOUR_USERNAME}
spring.datasource.password = {YOUR_PASSWORD}
```

To start SpringBoot:

```bash
cd backend
./gradleW bootRun
```

## to set up llm on your local emulator -- use Pixel 8
follow: https://github.com/briankim113/ChatPet/blob/main/app/README.md

download link for ```gemma3-1b-it-int4.task```: https://huggingface.co/litert-community/Gemma3-1B-IT#:~:text=Download%20and%20install%20the%20apk.
