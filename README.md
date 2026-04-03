# Travel-Destination-Planner-Destinex
Destinex is a travel destinations web application that allows admins to manage destinations and users to explore, save, and interact with destinations. Built with Spring Boot 4 for the backend and Angular for the frontend.

<img width="1267" height="590" alt="image" src="https://github.com/user-attachments/assets/5d0f8543-f8f0-4687-a7df-c5c2fd3fc8f5" />

## 🚀 Features

### Admin Dashboard
- Login for admins
- Fetch and view destination suggestions from external APIs (e.g., REST Countries API)
- Add, remove, or bulk-add destinations to the internal database
- Search destinations by name in external APIs
- View curated catalog of suggested destinations

### User Dashboard
- Login for users
- Explore all approved destinations
- View detailed information: country, capital, region, population, currency, flag/image
- Mark destinations as “Want to Visit” and view them in a Saved/Wishlist page
- Pagination for easier navigation


## 🛠️ Technologies Used
- **Backend:** Spring Boot 4.0.4, Java 25, Maven, MySQL
- **Frontend:** Angular, TypeScript, HTML, CSS
- **External APIs:** REST Countries API
  
  ![Java](https://img.shields.io/badge/java-25-brightgreen)
  ![Maven](https://img.shields.io/badge/maven-4.0.4-blue)
  ![Spring Boot](https://img.shields.io/badge/spring_boot-4.0.4-brightgreen)
  ![Angular](https://img.shields.io/badge/angular-21-red)
  ![MySQL](https://img.shields.io/badge/mysql-8-blue)

## 💻 Prerequisites

To run this project locally, ensure you have installed:

- Java 25 (JDK)
- Maven
- Node.js 20+ and npm
- MySQL database

## ⚡ Setup & Run Instructions

### Backend (Spring Boot)

1. Clone the repository:
   ```bash
   git clone https://github.com/Seifeldin-Ahmed/Travel-Destination-Planner-Destinex.git
   cd Destinex-backend
   ```
2. Configure your database in `src/main/resources/application.properties`:
   
    ```properties
    spring.datasource.url = jdbc:mysql://localhost:3306/travel_planner
    spring.datasource.username = <your-username>
    spring.datasource.password = <your-password>
    ```
4. Build and run the backend:
   ```bash
    ./mvnw spring-boot:run
   ```
5. Backend runs on `http://localhost:8080`

### Frontend (Angular)
1. Navigate to the frontend folder:
   
   ``` bash
   cd Destinx-frontend
   ```
3. Install dependencies:
   
    ``` bash
    npm install
    ```
5. Start the Angular dev server:
   
    ``` bash
    ng serve
    ```
7. Frontend runs on `http://localhost:4200`

## 📝 Test Credentials
Use the following accounts to test the application:
| 🏷️ Role | 📧 Email Address | 🔐 Password |
|:---:|:---|:---:|
| 👤 **User** | `john@luv2code.com` | `fun123` |
| 👑 **Admin** | `mary@luv2code.com` | `fun123` |
