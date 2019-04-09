# Watchlist

## Database requirements
The database must be configured to support utf characters and emojis (utf8mb4 for MySQL)

On mysql you can use:
```sql
ALTER DATABASE watchlist CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
```
