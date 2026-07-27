# Auth Service API

Base path: `/api/v1`

Public endpoints:

- `POST /auth/register`
- `POST /auth/login`
- `POST /auth/refresh`
- `POST /auth/forgot-password`
- `POST /auth/email-verification`

Authenticated endpoints:

- `POST /auth/logout`
- `POST /auth/change-password`

Administrative RBAC endpoints:

- `GET /users`
- `GET /users/{id}`
- `POST /users`
- `PUT /users/{id}`
- `DELETE /users/{id}`
- `GET /roles`
- `GET /roles/{id}`
- `POST /roles`
- `PUT /roles/{id}`
- `DELETE /roles/{id}`
- `GET /permissions`
- `GET /permissions/{id}`
- `POST /permissions`
- `PUT /permissions/{id}`
- `DELETE /permissions/{id}`

Errors follow the shared format:

```json
{
  "timestamp": "2026-07-21T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Request validation failed",
  "path": "/api/v1/auth/login",
  "violations": []
}
```

