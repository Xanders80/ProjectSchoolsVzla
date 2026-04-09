# DevOps Specialist - CI/CD Prompt

## CI/CD para SMS con GitHub Actions

### Pipeline de Build y Test
```yaml
name: Build & Test
on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven
      - name: Build with Maven
        run: mvn -B package --file pom.xml
      - name: Run tests
        run: mvn -B test -Ptest
```

### Pipeline de Deploy
```yaml
name: Deploy
on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Build JAR
        run: mvn -B package -Pprod -DskipTests
      - name: Deploy to server
        uses: appleboy/scp-action@master
        with:
          host: ${{ secrets.DEPLOY_HOST }}
          username: ${{ secrets.DEPLOY_USER }}
          key: ${{ secrets.DEPLOY_KEY }}
          source: "target/*.jar"
          target: "/opt/sms/"
      - name: Restart service
        uses: appleboy/ssh-action@master
        with:
          host: ${{ secrets.DEPLOY_HOST }}
          username: ${{ secrets.DEPLOY_USER }}
          key: ${{ secrets.DEPLOY_KEY }}
          script: |
            systemctl restart school-management
            systemctl status school-management
```

### Profiles de Maven
- **dev:** Hot reload, H2 database, debug enabled
- **test:** H2 database, test data
- **prod:** MariaDB, optimizations, security hardening
- **h2:** H2 in-memory para desarrollo rápido

### Docker Compose para Desarrollo
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - DB_URL=jdbc:mariadb://db:3306/dbSchollAdm
    depends_on:
      - db
  db:
    image: mariadb:11
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: dbSchollAdm
    ports:
      - "3306:3306"
    volumes:
      - mariadb_data:/var/lib/mysql
volumes:
  mariadb_data:
```
