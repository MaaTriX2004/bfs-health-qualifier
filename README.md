# Bajaj Finserv Health - Java Qualifier Solution

This repository contains the Spring Boot solution for the **Bajaj Finserv Health Hiring Challenge**. The application is designed to be an automated client that interacts with the assessment APIs to generate a webhook, solve the assigned SQL problem, and submit the solution.

## 📋 Project Overview

According to the qualifier requirements, this application:
1.  **Starts automatically** (No external trigger or Controller endpoint required).
2.  **Registers the user** via the `generateWebhook` API.
3.  **Retrieves** a unique `webhookUrl` and `accessToken` (JWT).
4.  **Submits the SQL Solution** for **Question 1** (Odd Registration Number logic) to the server.

### 🛠 Tech Stack
* **Language:** Java 17
* **Framework:** Spring Boot 3.2.0
* **Build Tool:** Maven
* **Dependencies:** `spring-boot-starter-web`, `jackson-databind`

---

## 🚀 How to Run

You can run this application using the pre-compiled JAR file or by building it from source.

### Option 1: Run the JAR (Recommended)
If you have the JAR file (`test-1.0-SNAPSHOT.jar`), simply run:

```bash
java -jar test-1.0-SNAPSHOT.jar
````

### Option 2: Build and Run from Source

1.  **Clone the repository:**

    ```bash
    git clone [https://github.com/MaaTriX2004/bajaj-health-qualifier-v2.git](https://github.com/MaaTriX2004/bajaj-health-qualifier-v2.git)
    cd bajaj-health-qualifier-v2
    ```

2.  **Build the project using Maven:**

    ```bash
    mvn clean package
    ```

3.  **Run the application:**

    ```bash
    java -jar target/test-1.0-SNAPSHOT.jar
    ```

-----

## 🧩 Logic & Implementation Details

### 1\. Automation (`WebhookRunner.java`)

The application uses the `CommandLineRunner` interface to execute the logic immediately after the Spring Context loads. This ensures the requirements of "No controller/endpoint should trigger the flow" are met.

### 2\. SQL Solution (Question 1)

The application submits the solution for **Question 1**, which requires:

  * **Filtering:** Exclude payments made on the 1st day of the month.
  * **Goal:** Find the highest salaried employee per department based on the filtered payments.
  * **Output:** Department Name, Total Salary, Combined Employee Name, and Age.

**Submitted Query Logic:**
The solution uses a Common Table Expression (CTE) or Subquery to:

1.  Filter out payments where `DAY(PAYMENT_TIME) = 1`.
2.  Calculate total salary per employee.
3.  Rank employees within their department using `ROW_NUMBER()`.
4.  Select the top-ranked employee (Highest Salary) for each department.

<!-- end list -->

```sql
WITH EmpStats AS (
    SELECT 
        e.DEPARTMENT, 
        e.FIRST_NAME, 
        e.LAST_NAME, 
        e.DOB, 
        SUM(p.AMOUNT) as TOTAL_SALARY 
    FROM EMPLOYEE e 
    JOIN PAYMENTS p ON e.EMP_ID = p.EMP_ID 
    WHERE DAY(p.PAYMENT_TIME) <> 1 
    GROUP BY e.EMP_ID, e.DEPARTMENT, e.FIRST_NAME, e.LAST_NAME, e.DOB 
), 
Ranked AS (
    SELECT 
        d.DEPARTMENT_NAME, 
        es.TOTAL_SALARY, 
        CONCAT(es.FIRST_NAME, ' ', es.LAST_NAME) as EMPLOYEE_NAME, 
        TIMESTAMPDIFF(YEAR, es.DOB, CURDATE()) as AGE, 
        ROW_NUMBER() OVER(PARTITION BY es.DEPARTMENT ORDER BY es.TOTAL_SALARY DESC) as rn 
    FROM EmpStats es 
    JOIN DEPARTMENT d ON es.DEPARTMENT = d.DEPARTMENT_ID 
) 
SELECT DEPARTMENT_NAME, TOTAL_SALARY as SALARY, EMPLOYEE_NAME, AGE 
FROM Ranked 
WHERE rn = 1 
ORDER BY DEPARTMENT_NAME;
```

-----

## 👤 Author

**Nipun Poswal**

  * **Registration No:** 22BCE3091 (Odd - Assigned Question 1)

<!-- end list -->
