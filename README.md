Watchlist
=========
Track all the movies that you're wishing to watch, you'd watched or you're
waiting for, the incoming releases and suggestions based on your preferences, build
yor own list and share with friends and family.

## How does it works? (Work in progress)
This is the backend service (Build with python and django rest framework),
the functionality is exposed as web services to be consumed from web and
mobile clients

## How to work on it?
1. Clone this repo.
2. Install the requirements through pip: `pip install -r ./requirements.txt`
3. Setup your database connection in: `./.watchlist/watchlist/settings.py`
4. Go to main app workspace: `cd watchlist`
4. Run the migrations: `python manage.py migrate core`
5. Insert a few test data: `bash refreshDb.sh`

## Api documentation/specification
The endpoints has been documented using the Open Api Specification (OAS3).
Checkout the `docs/api.yml` or go to `docs/api.html` in your browser to
read it.

## Database requirements
The database must be configured to support utf characters and emojis
(utf8mb4 for MySQL)

On mysql you can use:
```sql
ALTER DATABASE watchlist CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
```
