## Few-shot: API Endpoints
- GET /students
- POST /students
- GET /students/{id}
- PUT /students/{id}
- DELETE /students/{id}

Example: POST /students
Request body:
{
  "name": "John Doe",
  "email": "john@example.com",
  "enrollmentDate": "2024-09-01"
}
Response: 201 Created with created resource
