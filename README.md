
# Smart Garage – Car Service Management System

## 📖 Overview

**Smart Garage** е напълно функционално уеб приложение за автосервизи.  
То позволява управление на **клиенти, автомобили, услуги и сервизни посещения**.  

- Backend → **Spring Boot REST API** (session-based authentication, no MVC).  
- Frontend → **Next.js** (modern React stack).  
- Database → **MariaDB**.  

Проектът следва най-добрите практики за архитектура, сигурност и REST API документация.

---

## 🎯 Features

- 🔐 **Authentication (Sessions)** – вход, изход, смяна/забравена парола  
- 👥 **Customers** – регистрация, управление на профил, история на посещения  
- 🚘 **Vehicles** – свързване с клиенти, VIN/регистрационен номер, история на ремонти  
- 🛠️ **Services** – CRUD операции, филтриране и сортиране по цена/име  
- 📑 **Visits** – история, статус („in progress“, „ready for pickup“), PDF отчети  
- 🧾 **Reports** – PDF фактури с валутна конверсия и изпращане по имейл  
- 📧 **Email Notifications** – автоматично изпращане на профил данни 
- 🛠️ **Admin Tools** – заявки за работа, рейтинги, програми за лоялност, дистанционни посещения  

---

## 🧰 Tech Stack

### Backend
- **Java 17**, **Spring Boot**
- **Spring Security** (Session-based auth)
- **Spring Data JPA** + Hibernate
- **MariaDB**
- **Swagger / OpenAPI** – API документация
- **Lombok**
- **JUnit + Mockito** – тестове

### Frontend
- **Next.js 14** (App Router, TypeScript)
- **shadcn-ui** – UI компоненти
- **dice-ui** – Data Table компоненти
- **TanStack Query + Table** – fetch, филтриране, сортиране
- 🧾 **Reports** – генериране на PDF файлове директно във frontend-а (Next.js) с данни от REST API  
- 📧 **Email Notifications** – backend изпраща имейли за нови профили, но **PDF файлове се създават и свалят само от клиента**
- **pnpm** – пакетен мениджър

---

## 📁 Project Structure

```
SmartGarageRepo/
├── backend/
│   ├── src/
│   │   ├── ...
│   ├── package.json
│   └── ...
├── frontend/
│   ├── src/
│   │   ├── ...
│   ├── package.json
│   └── ...
├── LICENSE
└── README.md
```

---

## 🗄️ Database Setup (MariaDB)

1. Инсталирайте [MariaDB](https://mariadb.org/).  
2. Създайте база данни:  
   ```sql
   CREATE DATABASE smartgarage;
   ```
3. Стартирайте `schema.sql` и `data.sql` (намират се в `/backend/resources/`).  
4. Конфигурирайте `application.properties`:  

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/smartgarage
spring.datasource.username=your_user
spring.datasource.password=your_pass
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## 🚀 Running the Project

### Backend (Spring Boot API)
```bash
cd backend
./mvnw spring-boot:run
```
- REST API → `http://localhost:8080/api`  
- Swagger UI → `http://localhost:8080/swagger-ui.html`  

### Frontend (Next.js)
```bash
cd frontend\SmartGarageFrontend
pnpm install
pnpm dev
```
- Client → `http://localhost:3000`

---

## 📌 Contribution Guidelines

- Използвайте **feature branches** (`feature/auth`, `feature/vehicles`, …)  
- **Комит съобщения** → [Conventional Commits](https://www.conventionalcommits.org/)  
- Спазвайте принципи **SOLID**, **DRY**, **KISS**  
- Тествайте преди merge  

---

## 🔗 Useful Links

- 📘 **Backend API Docs** → `http://localhost:8080/swagger-ui.html`    
---
