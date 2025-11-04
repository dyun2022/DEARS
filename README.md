## Frontend

Need git-lfs

On macOS:
To install brew: 
```
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
eval "$(/opt/homebrew/bin/brew shellenv)"
```

Run:
```
brew install git-lfs
git lfs install
```

## Backend

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

## Emulator -- use Pixel 8
To run on emulator:
* go to Device Manager > three dots next to Pixel 8 > Additional Settings
* increase internal storage to 16 gb and expanded storage to 2 gb
* increase to 8 cores in CPU
