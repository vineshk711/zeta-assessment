# AI-Enhanced Attendance Operations Platform

A Spring Boot application that handles employee attendance via APIs and integrates an AI assistant for generating insights and summaries.

## Features

- REST APIs for attendance management
- PostgreSQL database for data storage
- OpenAI integration for generating attendance insights
- Docker support for easy deployment
- Load testing support with k6

## Prerequisites

- Java 17 or later
- Docker and Docker Compose
- OpenAI API key
- Maven

## Setup

1. Clone the repository
2. Set up your OpenAI API key in one of the following ways:
   - As an environment variable:
     ```bash
     export OPENAI_API_KEY=your_api_key_here
     ```
   - Edit `application-dev.yml` for local development
   - Supply it as a Docker environment variable:
     ```bash
     docker-compose up -e OPENAI_API_KEY=your_api_key_here
     ```

3. Build and run the application using Docker Compose:
   ```bash
   docker-compose up --build
   ```

The application will be available at `http://localhost:8080`

## API Endpoints

### Attendance Management

- `POST /api/attendance/check-in/{employeeId}` - Record employee check-in
- `POST /api/attendance/check-out/{attendanceId}` - Record employee check-out
- `POST /api/attendance/work-from-home/{employeeId}` - Record work from home
- `GET /api/attendance/employee/{employeeId}` - Get employee attendance records
- `GET /api/attendance/work-from-home/count/{employeeId}` - Get work from home count

### AI Insights

- `GET /api/ai/attendance/summary` - Get daily attendance summary
- `GET /api/ai/attendance/question` - Ask questions about attendance data

## Load Testing

To run load tests using k6:

1. Install k6:
   ```bash
   brew install k6
   ```

2. Run the load test:
   ```bash
   k6 run load-test.js
   ```

## Development

To run the application locally without Docker:

1. Start PostgreSQL:
   ```bash
   docker-compose up postgres -d
   ```

2. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## Troubleshooting

### OpenAI API Key
If you see the warning `The "OPENAI_API_KEY" variable is not set. Defaulting to a blank string`, make sure to properly configure the OpenAI API key as described in the Setup section.

## Database Schema

The application uses the following main tables:

- `employees` - Stores employee information
- `attendance` - Stores attendance records

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request 