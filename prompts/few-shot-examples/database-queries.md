## Few-shot: Database Queries
- Find active students with pagination
```sql
SELECT * FROM students WHERE deleted = FALSE LIMIT 20 OFFSET 0;
```
- Count enrollments per course
```sql
SELECT course_id, COUNT(*) FROM enrollments GROUP BY course_id;
```
- Retrieve grades for a student ordered by date
```sql
SELECT * FROM grades WHERE student_id = ? ORDER BY date DESC;
```
