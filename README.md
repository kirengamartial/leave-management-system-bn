# Leave Management System – Microservices Backend

## Getting Started

To clone this repository, run:

```bash
git clone https://github.com/kirengamartial/leave-management-system-bn.git
```

---

## Overview

This repository contains the backend microservices for the Leave Management System, designed with a modern microservices architecture. The system is deployed on an AWS EC2 instance, with all services containerized using Docker and orchestrated via Docker Compose. The public domain is secured with SSL and managed via AWS Route 53.

**Production Domain:**  
[https://leave-system.martialkirenga.engineer](https://leave-system.martialkirenga.engineer)

---

## Architecture

### Microservices

- **API Gateway**  
  - Acts as the single entry point for all client requests.
  - Handles routing, load balancing, and CORS.
  - Docker image: [`martial123/api-gateway`](https://hub.docker.com/r/martial123/api-gateway)

- **Auth Service**  
  - Handles user authentication, registration, JWT issuance, and Google OAuth2 login.
  - Manages user roles and profile information.
  - Docker image: [`martial123/auth-service`](https://hub.docker.com/r/martial123/auth-service) 

- **Leave Service**  
  - Manages leave requests, approvals, balances, and leave history.
  - Handles business logic for leave management.
  - Docker image: [`martial123/leave-service`](https://hub.docker.com/r/martial123/leave-service)

- **Database**  
  - PostgreSQL is used as the primary data store for all services.

- **Caddy**  
  - Serves as a reverse proxy for all backend services.
  - Automatically provisions and renews SSL certificates for HTTPS using Let's Encrypt.

---

## Deployment & Orchestration

- All services are containerized and published to Docker Hub.
- Docker Compose is used to orchestrate the services, ensuring seamless communication and scaling.
- The system is deployed on an AWS EC2 instance for high availability and scalability.
- Caddy is used as a reverse proxy to provide HTTPS for all services.
- AWS Route 53 is used to manage DNS records and route traffic to the EC2 instance.

---

## Environment Variables

**⚠️ Never commit secrets (client secrets, passwords, API keys, etc.) to your repository.**

Below is an example of the required environment variables for the microservices. Copy these into your `.env` file for local development and replace the placeholder values with your own secrets:

```env
# Database Configuration
DB_URL=your-database-url
DB_USERNAME=your-db-username
DB_PASSWORD=your-db-password

# JWT Configuration
JWT_SECRET=your-jwt-secret
JWT_EXPIRATION=86400000

# Google OAuth2 Configuration
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret
GOOGLE_REDIRECT_URI=your_backend_url/api/v1/auth/oauth2/redirect

# Mail Configuration
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@example.com
MAIL_PASSWORD=your-email-password

# Domain Configuration
DOMAIN_URL=your_backend_url
FRONTEND_URL_LOGIN=your_frontend_url/login

#CLOUDINARY
# Database Configuration
DB_URL=your_url
DB_USERNAME=
DB_PASSWORD=

# JWT Configuration
JWT_SECRET=your_secret
JWT_EXPIRATION=86400000

# Google OAuth2 Configuration
GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=
GOOGLE_REDIRECT_URI=https://leave-system.martialkirenga.engineer/api/v1/auth/oauth2/redirect

# Mail Configuration
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=
MAIL_PASSWORD=

# Domain Configuration
DOMAIN_URL=https://leave-system.martialkirenga.engineer
FRONTEND_URL_LOGIN=https://leave-management-system-fn.vercel.app/login

CLOUDINARY_CLOUD_NAME=your_cloudinary_name
CLOUDINARY_API_KEY=your_key
CLOUDINARY_API_SECRET=your_secret
```

---

## Useful Links

- **API Gateway Docker Image:** [martial123/api-gateway](https://hub.docker.com/r/martial123/api-gateway)
- **Production Domain:** [https://leave-system.martialkirenga.engineer](https://leave-system.martialkirenga.engineer)

---

## How It Works

1. **API Gateway** receives all incoming requests at the public domain and routes them to the appropriate backend service.
2. **Auth Service** manages authentication, user registration, JWT, and OAuth2 flows.
3. **Leave Service** handles all leave-related business logic and data management.
4. **PostgreSQL** stores persistent data for all services.
5. **Caddy** ensures all traffic is encrypted with SSL and forwards requests to the API Gateway.
6. **Route 53** manages DNS and ensures reliable routing to the AWS EC2 instance.

---

## Running Locally (Example)

1. Clone the repository.
2. Ensure Docker and Docker Compose are installed.
3. Configure environment variables as needed for each service.
4. Run `docker-compose up --build` from the project root.

---

## Notes

- Each microservice is independently deployable and scalable.
- All images are stored on Docker Hub for easy deployment.
- The system is secured with SSL via Caddy and DNS is managed via AWS Route 53.
- **Frontend is not included in this documentation.** # leave-management-system-bn

