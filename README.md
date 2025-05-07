# EUCL Prepaid Token System

This is a Spring Boot application for managing prepaid electricity tokens, including user registration, authentication, meter registration, token purchasing, and notification checks for expiring tokens. The application uses JWT-based authentication, MySQL for persistence, and Spring Security for role-based access control.

## Prerequisites

-   **Java:** JDK 17
-   **Maven:** 3.8.x or higher
-   **MySQL:** 8.x
-   **Git:** For cloning the repository
-   **cURL or Postman:** For testing APIs

## Setup Instructions

1.  **Clone the Repository**
    ```bash
    git clone https://github.com/christejab07/euct-token-generation.git
    cd eucl-prepaid-token-system
    ```

2.  **Configure MySQL**
    -   Ensure MySQL is running locally or on a server.
    -   Create a database named `eucl_db` (the application will create tables automatically):
        ```sql
        CREATE DATABASE eucl_db;
        ```

3.  **Configure `application.yml`**
    -   Copy the template below to `src/main/resources/application.yml`.
    -   Set the environment variables `JWT_SECRET` and `DB_PASSWORD` for security.

    **`application.yml` Format:**
    ```yaml
    spring:
      datasource:
        url: jdbc:mysql://localhost:3306/eucl_db?createDatabaseIfNotExist=true
        username: root
        password: ${DB_PASSWORD} # Replace with your password or use environment variables below
      jpa:
        hibernate:
          ddl-auto: update
        show-sql: true
        properties:
          hibernate:
            dialect: org.hibernate.dialect.MySQL8Dialect
    logging:
      level:
        com.example.eucl_token: DEBUG
        org.springframework: WARN
        org.hibernate: WARN
    jwt:
      secret: ${JWT_SECRET} # Replace with your JWT secret or use environment variables below
      expiration-ms: 900000 # 15 minutes
    ```

    **Set Environment Variables:**

    -   **Linux/macOS:**
        ```bash
        export JWT_SECRET=$(openssl rand -base64 32)
        export DB_PASSWORD=your_mysql_password
        ```
    -   **Windows (Command Prompt):**
        ```bash
        setx JWT_SECRET $(openssl rand -base64 32)
        setx DB_PASSWORD your_mysql_password
        ```
        Replace `your_mysql_password` with your MySQL root password.

    > **Note:** Do not commit `application.yml` to Git, as it’s excluded by `.gitignore` to protect sensitive data.

4.  **Build and Run the Application**
    ```bash
    mvn clean install
    mvn spring-boot:run
    ```
    The application will start on `http://localhost:8080`.
    Check the console for errors (e.g., missing `JWT_SECRET` or database connection issues).

5.  **Test APIs**
    Use cURL or Postman to test the APIs. Below are example requests and expected responses.

    **Register a User**
    ```bash
    curl -X POST http://localhost:8080/api/users/signup \
    -H "Content-Type: application/json" \
    -d '{"name":"John Doe","email":"john.doe@example.com","phone":"1234567890","nationalId":"1234567890123456","password":"secure123","role":"ROLE_CUSTOMER"}'
    ```
    **Response (HTTP 200):**
    ```json
    {
      "id": 1,
      "name": "John Doe",
      "email": "john.doe@example.com",
      "phone": "1234567890",
      "nationalId": "123456789012",
      "role": "ROLE_CUSTOMER"
    }
    ```

    **Login**
    ```bash
    curl -X POST http://localhost:8080/api/users/login \
    -H "Content-Type: application/json" \
    -d '{"email":"john.doe@example.com","password":"secure123"}'
    ```
    **Response (HTTP 200):**
    ```
    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    ```
    Save the JWT for protected endpoints.

    **Register a Meter**
    ```bash
    curl -X POST http://localhost:8080/api/meters/register \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer <your-jwt-token>" \
    -d '{"meterNumber":"123456","userId":1}'
    ```
    **Response (HTTP 200):**
    ```json
    {
      "id": 1,
      "meterNumber": "123456",
      "userId": 1
    }
    ```

    **Purchase a Token**
    ```bash
    curl -X POST http://localhost:8080/api/tokens/purchase \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer <your-jwt-token>" \
    -d '{"meterNumber":"123456","amount":100.0}'
    ```
    **Response (HTTP 200):**
    ```json
    {
      "id": 1,
      "tokenCode": "1234567890123456",
      "meterNumber": "123456",
      "amount": 100.0,
      "purchaseDate": "2025-05-07T12:00:00",
      "expiryDate": "2025-06-06T12:00:00"
    }
    ```

    **Check Expiring Tokens (Admin Only)**
    ```bash
    # Register an admin:
    curl -X POST http://localhost:8080/api/users/signup \
    -H "Content-Type: application/json" \
    -d '{"name":"Admin User","email":"admin@example.com","phone":"0987654321","nationalId":"987654321098","password":"admin123","role":"ROLE_ADMIN"}'

    # Login as admin:
    curl -X POST http://localhost:8080/api/users/login \
    -H "Content-Type: application/json" \
    -d '{"email":"admin@example.com","password":"admin123"}'

    # Check expiring tokens:
    curl -X POST http://localhost:8080/api/notifications/check-expiring \
    -H "Authorization: Bearer <admin-jwt-token>"
    ```
    **Response (HTTP 200):**
    ```json
    {}
    ```

## Troubleshooting

-   **Missing `JWT_SECRET`:** Ensure the `JWT_SECRET` environment variable is set. Check with `echo $JWT_SECRET` (Linux/macOS) or `echo %JWT_SECRET%` (Windows).
-   **Database Errors:** Verify MySQL is running and `DB_PASSWORD` is correct. Check the application logs for detailed error messages.
-   **`401 Unauthorized`:** Ensure the JWT is valid and included in the `Authorization: Bearer` header for protected endpoints.
-   **`403 Forbidden`:** For `/api/notifications/check-expiring`, make sure you are using a JWT obtained after logging in as a user with the `ROLE_ADMIN` role.

## Security Notes

-   **Do not commit `application.yml`:** It’s excluded by `.gitignore` to protect `JWT_SECRET` and `DB_PASSWORD`.
-   **Generate a secure `JWT_SECRET`:** Use `openssl rand -base64 32` for production environments to create a strong secret.
-   **Rotate secrets regularly:** Update `JWT_SECRET` periodically to mitigate potential security risks.

## Contributing

1.  Fork the repository.
2.  Create a feature branch (`git checkout -b feature/your-feature`).
3.  Commit changes (`git commit -m "Add your feature"`).
4.  Push to the branch (`git push origin feature/your-feature`).
5.  Open a pull request.
