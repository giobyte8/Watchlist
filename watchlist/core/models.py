from django.db import models
from django.utils import timezone


class Role(models.Model):
    name = models.CharField(max_length=255)

    class Meta:
        db_table = 'role'


class User(models.Model):
    picture = models.CharField(max_length=5000)
    name = models.CharField(max_length=500)
    email = models.CharField(max_length=1000)
    created_at = models.DateTimeField(default=timezone.now)
    updated_at = models.DateTimeField(default=timezone.now)
    role = models.ForeignKey(
        Role,
        on_delete=models.PROTECT,
        related_name='users'
    )

    class Meta:
        db_table = 'user'


class Watchlist(models.Model):
    name = models.CharField(max_length=255)
    is_default_list = models.BooleanField(default=False)
    deleted = models.BooleanField(default=False)
    created_by = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        db_column='created_by',
        related_name='+'
    )
    created_at = models.DateTimeField(default=timezone.now)
    updated_at = models.DateTimeField(default=timezone.now)

    class Meta:
        db_table = 'watchlist'


class WatchlistPermission(models.Model):
    name = models.CharField(max_length=255)

    class Meta:
        db_table = 'watchlist_permission'


class UserHasWatchlist(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='+')
    shared_by = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        db_column='shared_by',
        related_name='+'
    )
    watchlist = models.ForeignKey(
        Watchlist,
        on_delete=models.CASCADE,
        related_name='+'
    )
    permission = models.ForeignKey(
        WatchlistPermission,
        on_delete=models.CASCADE,
        db_column='watchlist_permission_id',
        related_name='+'
    )
    created_at = models.DateTimeField(default=timezone.now)
    updated_at = models.DateTimeField(default=timezone.now)

    class Meta:
        db_table = 'user_has_watchlist'


class Genre(models.Model):
    name: models.CharField(255)

    class Meta:
        db_table = 'genre'


class Movie(models.Model):
    tmdb_id = models.IntegerField()
    title = models.CharField(max_length=255)
    original_title = models.CharField(max_length=255)
    release_date = models.DateField(),
    runtime = models.IntegerField(),
    synopsis = models.CharField(max_length=5000)
    rating = models.FloatField()
    created_at = models.DateTimeField(default=timezone.now)
    updated_at = models.DateTimeField(default=timezone.now)
    genres = models.ManyToManyField(
        Genre,
        db_table='movie_has_genre',
        related_name='+'
    )

    class Meta:
        db_table = 'movie'


class WatchlistHasMovie(models.Model):
    seen: models.BooleanField(default=False)
    added_at = models.DateTimeField(default=timezone.now)
    seen_at = models.DateTimeField()
    watchlist = models.ForeignKey(
        Watchlist,
        on_delete=models.CASCADE,
        related_name='hasMovies'
    )
    movie = models.ForeignKey(
        Movie,
        on_delete=models.CASCADE,
        related_name='+'
    )
    added_by = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        db_column='added_by',
        related_name='+'
    )

    class Meta:
        db_table = 'watchlist_has_movie'


class PictureCategory(models.Model):
    name = models.CharField(max_length=255)

    class Meta:
        db_table = 'picture_category'


class Picture(models.Model):
    url = models.CharField(max_length=5000)
    movie = models.ForeignKey(
        Movie,
        on_delete=models.CASCADE,
        related_name='pictures'
    )
    category = models.ForeignKey(
        PictureCategory,
        on_delete=models.CASCADE,
        db_column='picture_category',
        related_name='+'
    )

    class Meta:
        db_table = 'picture'


class CrewCategory(models.Model):
    name = models.CharField(max_length=255)

    class Meta:
        db_table = 'crew_category'


class Crew(models.Model):
    name = models.CharField(max_length=500)
    character_name = models.CharField(max_length=500)
    picture_url = models.CharField(max_length=5000, null=True)
    movie = models.ForeignKey(Movie, on_delete=models.CASCADE, related_name='crew')
    category = models.ForeignKey(
        CrewCategory,
        on_delete=models.CASCADE,
        db_column='crew_category_id',
        related_name='+'
    )

    class Meta:
        db_table = 'crew'
