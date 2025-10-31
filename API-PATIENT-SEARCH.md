# Patient Search by Aadhaar API

## Endpoint Details

**URL:** `GET /api/v1/patients/search/aadhaar/{aadhaarNumber}`

**Authorization:** Requires `DOCTOR` role

**Description:** Allows doctors to search for patients using their Aadhaar number. The Aadhaar number is encrypted before searching the database for security.

## Request

### Path Parameter
- `aadhaarNumber` (string, required): Patient's 12-digit Aadhaar number

### Headers
```
Authorization: Bearer <JWT_TOKEN>
```

## Example Request

```bash
curl -X GET \
  'http://localhost:8080/api/v1/patients/search/aadhaar/123456789012' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...'
```

## Response

### Success Response (200 OK)

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "patient@example.com",
  "phoneNumber": "+919876543210",
  "isActive": true,
  "mfaEnabled": false,
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "1990-01-15",
  "gender": "MALE",
  "bloodGroup": "O_POSITIVE",
  "address": "123 Main Street, City, State, PIN",
  "allergies": "Peanuts, Penicillin",
  "chronicConditions": "Diabetes Type 2",
  "emergencyContactName": "Jane Doe",
  "emergencyContactPhone": "+919876543211",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-10-31T05:00:00"
}
```

### Error Responses

#### Patient Not Found (404)
```json
{
  "timestamp": "2024-10-31T05:00:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Patient not found with provided Aadhaar number",
  "path": "/api/v1/patients/search/aadhaar/123456789012"
}
```

#### Unauthorized (401)
```json
{
  "timestamp": "2024-10-31T05:00:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource"
}
```

#### Forbidden (403)
```json
{
  "timestamp": "2024-10-31T05:00:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied - Requires DOCTOR role"
}
```

## Security Notes

1. **Encryption:** The Aadhaar number is encrypted using AES-256 before database lookup
2. **Authorization:** Only users with `DOCTOR` role can access this endpoint
3. **Audit:** All patient lookups are logged for audit purposes
4. **Privacy:** The Aadhaar number is NOT returned in the response for security reasons

## Frontend Integration Example

```typescript
// Example using axios
const searchPatientByAadhaar = async (aadhaarNumber: string) => {
  try {
    const response = await axios.get(
      `http://localhost:8080/api/v1/patients/search/aadhaar/${aadhaarNumber}`,
      {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken')}`
        }
      }
    );
    return response.data;
  } catch (error) {
    if (error.response?.status === 404) {
      console.error('Patient not found');
    } else if (error.response?.status === 403) {
      console.error('Access denied - Doctor role required');
    }
    throw error;
  }
};
```

## Testing with Postman

1. **Login as Doctor** to get JWT token
2. **Copy the access token** from login response
3. **Make GET request** to `/api/v1/patients/search/aadhaar/123456789012`
4. **Add Authorization header:** `Bearer <your_token>`

## Related Endpoints

- `GET /api/v1/patients/profile` - Get current patient's own profile
- `GET /api/v1/patients/{patientId}` - Get patient by ID (Admin only)
