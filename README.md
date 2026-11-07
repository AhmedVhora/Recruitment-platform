# Recruitment & Resume Intelligence Platform API

Local base URL: `http://localhost:8080`  
Swagger UI: `http://localhost:8080/swagger-ui.html`  
OpenAPI JSON: `http://localhost:8080/v3/api-docs`

All endpoints except registration and login require:

```http
Authorization: Bearer <jwt-token>
```

All JSON bodies use `Content-Type: application/json`. `PATCH` bodies are partial: only supplied fields are changed. Invalid input returns `400`; a missing resource returns `404`; an ownership or role violation returns `403`; conflicts such as duplicates or protected deletion return `409`.

## Response shapes

| Name | JSON returned |
| --- | --- |
| `User` | `{ "id", "fullName", "email", "role", "createdAt", "updatedAt" }` |
| `Login` | `{ "token", "tokenType": "Bearer", "user": User }` |
| `Company` | `{ "id", "name", "description", "industry", "website", "location", "logoUrl", "createdById", "createdAt", "updatedAt" }` |
| `MembershipRequest` | `{ "id", "companyId", "companyName", "targetRecruiterId", "requestType", "requestStatus", "initiatedById" }` |
| `Job` | `{ "id", "companyId", "companyName", "createdById", "title", "jobCategory", "description", "responsibilities", "requiredSkills", "qualifications", "minExperience", "employmentType", "workMode", "location", "minSalary", "maxSalary", "totalVacancies", "remainingVacancies", "applicationDeadline", "status", "createdAt", "updatedAt" }` |
| `Application` | `{ "id", "jobId", "jobTitle", "status", "resumeUrl", "appliedAt", "updatedAt" }` |
| `Profile` | `{ "id", "userId", "headline", "professionalSummary", "location", "yearsOfExperience", "linkedinUrl", "githubUrl", "portfolioUrl", "skills", "resumeUrls", "projects", "certifications", "education", "experiences", "createdAt", "updatedAt" }` |
| `RecruiterInterview` | `{ "id", "applicationId", "roundNumber", "totalRounds", "roundName", "scheduledAt", "durationMinutes", "mode", "meetingLink", "location", "interviewerId", "interviewerName", "status", "outcome", "feedback", "strengths", "areasForImprovement", "feedbackSubmittedAt", "cancellationReason", "cancelledAt", "createdAt", "updatedAt" }` |
| `CandidateInterview` | `RecruiterInterview` without `feedback`, `strengths`, `areasForImprovement`, or `feedbackSubmittedAt` |
| `Page<T>` | `{ "content": [T], "totalElements", "totalPages", "size", "number" }` |

`Page<T>` endpoints accept `page` (zero-based), `size` (maximum `100`), and, where supported, `sort`. The default size is `20`; Company endpoints explicitly default to `10`.

## Candidate side

### Shared account APIs

| Method and path | JSON request | Returns | What it does |
| --- | --- | --- | --- |
| `POST /users/register` | `{ "fullName", "email", "password", "role": "CANDIDATE" }` | `201 User` | Creates a candidate account. Public registration also allows `RECRUITER`, never `ADMIN`. |
| `POST /users/login` | `{ "email", "password" }` | `200 Login` | Authenticates and returns a JWT. |
| `GET /users/me` | none | `200 User` | Returns the authenticated account. |
| `PATCH /users/me` | `{ "fullName"?, "role"? }` | `200 User` | Updates the current account; `ADMIN` cannot be selected. |
| `PATCH /users/me/password` | `{ "currentPassword", "newPassword" }` | `204` | Changes the current password. |
| `POST /users/logout` | none | `204` | Revokes the bearer token. |
| `DELETE /users/me` | `{ "password" }` | `204` or `409` | Deletes an unreferenced account. Related applications, interviews, jobs, memberships, companies, or profiles protect history and cause `409`. |

### Company discovery

| Method and path | JSON request | Returns | What it does |
| --- | --- | --- | --- |
| `GET /companies/all_companies?page=&size=&sort=` | none | `200 Page<Company>` | Browses companies. |
| `GET /companies/{company_id}` | none | `200 Company` | Gets one company. |
| `GET /companies/search?name=&page=&size=` | none | `200 Page<Company>` | Searches normalized company names. |

### Job discovery and applications

| Method and path | JSON request | Returns | What it does |
| --- | --- | --- | --- |
| `GET /jobs?keyword=&jobCategory=&location=&employmentType=&workMode=&minExperience=&minSalary=&maxSalary=&companyId=&page=&size=&sort=` | none | `200 Page<Job>` | Searches available published jobs. `sort` is `newest`, `salary_asc`, or `salary_desc`. |
| `GET /jobs/{jobId}` | none | `200 Job` | Gets an available published job. |
| `POST /api/jobs/{jobId}/applications` | `{ "resumeUrl" }` | `201 Application` | Applies using a resume URL belonging to the candidate profile. Duplicate applications are rejected. |
| `GET /api/applications/me?status=&page=&size=` | none | `200 Page<Application>` | Lists the candidate's applications. |
| `GET /api/applications/{applicationId}` | none | `200 Application` | Gets the candidate's own application. |
| `DELETE /api/applications/{applicationId}` | none | `204` | Withdraws an `APPLIED` or `UNDER_REVIEW` application only. |

### Candidate profile

| Method and path | JSON request | Returns | What it does |
| --- | --- | --- | --- |
| `POST /profiles` | `{ "headline"?, "professionalSummary"?, "location"?, "yearsOfExperience"?, "linkedinUrl"?, "githubUrl"?, "portfolioUrl"?, "skills"?: ["Java"], "resumeUrls"?: ["https://..."] }` | `201 Profile` | Creates the candidate's one profile. |
| `GET /profiles/me` | none | `200 Profile` | Returns the candidate's profile. |
| `PATCH /profiles/me` | Same optional fields as profile creation | `200 Profile` | Updates profile details. |
| `POST /profiles/me/skills` | `{ "skills": ["Java", "Spring Boot"] }` | `200 Profile` | Adds normalized skills. |
| `DELETE /profiles/me/skills/{skill}` | none | `204` | Removes one skill. |
| `POST /profiles/me/projects` | `{ "projectName", "description"?, "role"?, "technologies"?, "projectUrl"?, "startDate"?, "endDate"? }` | `201 Profile` | Adds a project. |
| `PATCH /profiles/me/projects/{projectId}` | Same project fields, all optional | `200 Profile` | Updates one owned project. |
| `DELETE /profiles/me/projects/{projectId}` | none | `204` | Deletes one owned project. |
| `POST /profiles/me/certifications` | `{ "name", "issuingOrganization"?, "issueDate"?, "expirationDate"?, "credentialId"?, "credentialUrl"? }` | `201 Profile` | Adds a certification. |
| `PATCH /profiles/me/certifications/{certificationId}` | Same certification fields, all optional | `200 Profile` | Updates one owned certification. |
| `DELETE /profiles/me/certifications/{certificationId}` | none | `200 Profile` | Deletes one owned certification. |
| `POST /profiles/me/education` | `{ "institution", "degree"?, "fieldOfStudy"?, "startDate"?, "endDate"?, "grade"? }` | `201 Profile` | Adds education. |
| `PATCH /profiles/me/education/{educationId}` | Same education fields, all optional | `200 Profile` | Updates owned education. |
| `DELETE /profiles/me/education/{educationId}` | none | `200 Profile` | Deletes owned education. |
| `POST /profiles/me/experiences` | `{ "organizationName", "jobTitle", "location"?, "startDate"?, "endDate"?, "currentlyWorking", "description"? }` | `201 Profile` | Adds work experience. Current employment must not have an end date. |
| `PATCH /profiles/me/experiences/{experienceId}` | Same experience fields, all optional | `200 Profile` | Updates owned experience. |
| `DELETE /profiles/me/experiences/{experienceId}` | none | `200 Profile` | Deletes owned experience. |
| `DELETE /profiles/me` | none | `204` or `409` | Deletes a profile only when the candidate has no application history. |

### Candidate interviews

| Method and path | JSON request | Returns | What it does |
| --- | --- | --- | --- |
| `GET /interviews/me?page=&size=` | none | `200 Page<CandidateInterview>` | Lists the candidate's interview rounds. |
| `GET /interviews/{interviewId}` | none | `200 CandidateInterview` | Gets the candidate's own interview without private interviewer feedback. |

## Recruiter side

### Shared account APIs

| Method and path | JSON request | Returns | What it does |
| --- | --- | --- | --- |
| `POST /users/register` | `{ "fullName", "email", "password", "role": "RECRUITER" }` | `201 User` | Creates a recruiter account. Public registration also allows `CANDIDATE`, never `ADMIN`. |
| `POST /users/login` | `{ "email", "password" }` | `200 Login` | Authenticates and returns a JWT. |
| `GET /users/me` | none | `200 User` | Returns the authenticated account. |
| `PATCH /users/me` | `{ "fullName"?, "role"? }` | `200 User` | Updates the current account, except for `ADMIN`. |
| `PATCH /users/me/password` | `{ "currentPassword", "newPassword" }` | `204` | Changes the current password. |
| `POST /users/logout` | none | `204` | Revokes the bearer token. |
| `DELETE /users/me` | `{ "password" }` | `204` or `409` | Deletes an unreferenced account; related recruitment records protect history. |

### Company and recruiter membership

| Method and path | JSON request | Returns | What it does |
| --- | --- | --- | --- |
| `POST /companies/create_company` | `{ "name", "description"?, "industry"?, "website"?, "location"?, "logoUrl"? }` | `201 Company` | Creates a company and makes the creator a recruiter member. |
| `PATCH /companies/update_company/{company_id}` | Same company fields, all optional | `200 Company` | Updates a company; only its owner may do this. |
| `GET /companies/all_companies?page=&size=&sort=` | none | `200 Page<Company>` | Browses companies. |
| `GET /companies/{company_id}` | none | `200 Company` | Gets one company. |
| `GET /companies/search?name=&page=&size=` | none | `200 Page<Company>` | Searches company names. |
| `GET /companies/my_recruiter_companies` | none | `200 [Company]` | Lists companies where the recruiter is a member. |
| `GET /companies/my_owned_companies` | none | `200 [Company]` | Lists companies created by the recruiter. |
| `POST /companies/{companyId}/join-requests` | none | `201 MembershipRequest` | Requests membership in a company. |
| `GET /companies/{companyId}/join-requests?filter=ALL|PENDING|APPROVED|REJECTED|CANCELLED` | none | `200 [MembershipRequest]` | Owner lists join requests. |
| `PATCH /companies/{companyId}/join-requests/{requestId}` | `{ "status": "APPROVED" | "REJECTED" }` | `200 MembershipRequest` | Owner accepts or rejects a join request. |
| `POST /companies/{companyId}/invitations` | `{ "recruiterId" }` | `201 MembershipRequest` | Owner invites a recruiter. |
| `GET /companies/{companyId}/invitations?filter=ALL|PENDING|APPROVED|REJECTED|CANCELLED` | none | `200 [MembershipRequest]` | Owner lists invitations. |
| `PATCH /companies/{companyId}/invitations/{requestId}` | `{ "requestStatus": "APPROVED" | "REJECTED" }` | `200 MembershipRequest` | Invited recruiter accepts or rejects an invitation. |
| `GET /companies/{companyId}/recruiters` | none | `200 [{ "recruiterId", "fullName" }]` | Lists company recruiters. |
| `DELETE /companies/{companyId}/recruiters/{recruiterId}` | none | `204` | Owner removes a recruiter, except the owner. |
| `GET /companies/my_join_requests?filter=` | none | `200 [MembershipRequest]` | Lists the recruiter's own join requests. |
| `GET /companies/my_invitations?filter=` | none | `200 [MembershipRequest]` | Lists the recruiter's invitations. |

### Jobs

| Method and path | JSON request | Returns | What it does |
| --- | --- | --- | --- |
| `POST /jobs` | `{ "companyId", "title", "jobCategory", "description", "responsibilities"?, "requiredSkills", "qualifications"?, "minExperience"?, "employmentType", "workMode", "location"?, "minSalary"?, "maxSalary"?, "totalVacancies", "applicationDeadline" }` | `201 Job` | Creates a draft job for a company where the recruiter is a member. |
| `PATCH /jobs/{jobId}` | Same job fields except `companyId`; all optional | `200 Job` | Updates a job, including vacancies while preserving filled positions. |
| `DELETE /jobs/{jobId}` | none | `204` | Deletes a draft job only. |
| `GET /jobs/{jobId}` | none | `200 Job` | Gets a job belonging to the recruiter's company. |
| `GET /jobs/company/{companyId}?page=&size=&sort=` | none | `200 Page<Job>` | Lists jobs for one associated company. |
| `PATCH /jobs/{jobId}/publish` | none | `200 Job` | Publishes a valid draft job with vacancies. |
| `PATCH /jobs/{jobId}/close` | none | `200 Job` | Closes an associated published job. |
| `PATCH /jobs/{jobId}/reopen` | none | `200 Job` | Reopens an eligible closed job. |
| `GET /jobs?...` | none | `200 Page<Job>` | The same filtered job search available to candidates. |

### Applications and candidate profiles

| Method and path | JSON request | Returns | What it does |
| --- | --- | --- | --- |
| `GET /api/applications/{applicationId}` | none | `200 Application` | Gets an application for a job in the recruiter's company. |
| `GET /api/jobs/{jobId}/applications?status=&page=&size=` | none | `200 Page<Application>` | Lists applications for an associated job. |
| `PATCH /api/applications/{applicationId}/status` | `{ "status": "UNDER_REVIEW" | "SHORTLISTED" | "REJECTED" | "HIRED" }` | `200 Application` | Moves an application through valid transitions. `HIRED` atomically decreases vacancies. |
| `GET /profiles/{candidateId}` | none | `200 Profile` | Gets an authorized candidate profile in the recruitment context. |

### Interviews

| Method and path | JSON request | Returns | What it does |
| --- | --- | --- | --- |
| `POST /interviews/applications/{applicationId}` | `{ "roundName", "scheduledAt", "durationMinutes", "mode": "ONLINE" | "ONSITE", "meetingLink"?, "location"?, "interviewerId", "totalRounds"? }` | `201 RecruiterInterview` | Schedules the next interview for a shortlisted application. Online requires a meeting link; onsite requires a location. |
| `GET /interviews/me?page=&size=` | none | `200 Page<RecruiterInterview>` | Lists rounds assigned to the recruiter. |
| `GET /interviews/jobs/{jobId}?page=&size=` | none | `200 Page<RecruiterInterview>` | Lists interview rounds for an associated job. |
| `GET /interviews/{interviewId}` | none | `200 RecruiterInterview` | Gets one authorized interview round. |
| `PATCH /interviews/{interviewId}` | `{ "roundName"?, "scheduledAt"?, "durationMinutes"?, "mode"?, "meetingLink"?, "location"?, "interviewerId"? }` | `200 RecruiterInterview` | Updates a scheduled round. A cancelled round can reopen only if no later round exists. |
| `PATCH /interviews/{interviewId}/cancel` | `{ "cancellationReason"? }` | `200 RecruiterInterview` | Cancels a scheduled round. |
| `PATCH /interviews/{interviewId}/feedback` | `{ "feedback", "strengths"?, "areasForImprovement"?, "outcome": "PASSED" | "FAILED" }` | `200 RecruiterInterview` | Assigned interviewer submits feedback and completes the round. A failed outcome rejects the application when its transition is valid. |

## Important integrity rules

- User deletion is blocked when dependent recruitment records exist.
- Profile deletion is blocked once a candidate has at least one application. This keeps application and interview history tied to an existing profile.
- Draft jobs may be deleted; jobs with applications cannot be deleted through the API.
- Applications with interview history cannot be withdrawn because only `APPLIED` and `UNDER_REVIEW` applications are withdrawable.
- No Company-delete or Interview-delete endpoint exists.
- PostgreSQL migration is not part of this project; development uses H2.
