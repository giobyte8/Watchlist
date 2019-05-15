# Generated by Django 2.2 on 2019-05-15 03:21

from django.db import migrations, models
import django.db.models.deletion
import django.utils.timezone


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='AuthProvider',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=255)),
            ],
            options={
                'db_table': 'auth_provider',
            },
        ),
        migrations.CreateModel(
            name='CrewCategory',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=255)),
            ],
            options={
                'db_table': 'crew_category',
            },
        ),
        migrations.CreateModel(
            name='Genre',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=255)),
            ],
            options={
                'db_table': 'genre',
            },
        ),
        migrations.CreateModel(
            name='Movie',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('tmdb_id', models.IntegerField()),
                ('title', models.CharField(max_length=255)),
                ('original_title', models.CharField(max_length=255)),
                ('release_date', models.DateField()),
                ('runtime', models.IntegerField(default=0)),
                ('synopsis', models.CharField(max_length=5000)),
                ('rating', models.FloatField()),
                ('created_at', models.DateTimeField(default=django.utils.timezone.now)),
                ('updated_at', models.DateTimeField(default=django.utils.timezone.now)),
                ('genres', models.ManyToManyField(db_table='movie_has_genre', related_name='_movie_genres_+', to='core.Genre')),
            ],
            options={
                'db_table': 'movie',
            },
        ),
        migrations.CreateModel(
            name='PictureCategory',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=255)),
            ],
            options={
                'db_table': 'picture_category',
            },
        ),
        migrations.CreateModel(
            name='Role',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=255)),
            ],
            options={
                'db_table': 'role',
            },
        ),
        migrations.CreateModel(
            name='User',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('password', models.CharField(max_length=128, verbose_name='password')),
                ('last_login', models.DateTimeField(blank=True, null=True, verbose_name='last login')),
                ('picture', models.CharField(max_length=5000)),
                ('name', models.CharField(max_length=500)),
                ('email', models.CharField(max_length=1000)),
                ('created_at', models.DateTimeField(default=django.utils.timezone.now)),
                ('updated_at', models.DateTimeField(default=django.utils.timezone.now)),
                ('role', models.ForeignKey(on_delete=django.db.models.deletion.PROTECT, related_name='users', to='core.Role')),
            ],
            options={
                'db_table': 'user',
            },
        ),
        migrations.CreateModel(
            name='Watchlist',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=255)),
                ('is_default_list', models.BooleanField(default=False)),
                ('deleted', models.BooleanField(default=False)),
                ('created_at', models.DateTimeField(default=django.utils.timezone.now)),
                ('updated_at', models.DateTimeField(default=django.utils.timezone.now)),
            ],
            options={
                'db_table': 'watchlist',
            },
        ),
        migrations.CreateModel(
            name='WatchlistPermission',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=255)),
            ],
            options={
                'db_table': 'watchlist_permission',
            },
        ),
        migrations.CreateModel(
            name='WatchlistHasMovie',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('added_at', models.DateTimeField(default=django.utils.timezone.now)),
                ('seen_at', models.DateTimeField(null=True)),
                ('added_by', models.ForeignKey(db_column='added_by', on_delete=django.db.models.deletion.CASCADE, related_name='+', to='core.User')),
                ('movie', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='+', to='core.Movie')),
                ('watchlist', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='has_movies', to='core.Watchlist')),
            ],
            options={
                'db_table': 'watchlist_has_movie',
            },
        ),
        migrations.CreateModel(
            name='UserHasWatchlist',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('created_at', models.DateTimeField(default=django.utils.timezone.now)),
                ('updated_at', models.DateTimeField(default=django.utils.timezone.now)),
                ('permission', models.ForeignKey(db_column='watchlist_permission_id', on_delete=django.db.models.deletion.CASCADE, related_name='+', to='core.WatchlistPermission')),
                ('shared_by', models.ForeignKey(db_column='shared_by', null=True, on_delete=django.db.models.deletion.CASCADE, related_name='+', to='core.User')),
                ('user', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='+', to='core.User')),
                ('watchlist', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='+', to='core.Watchlist')),
            ],
            options={
                'db_table': 'user_has_watchlist',
            },
        ),
        migrations.AddField(
            model_name='user',
            name='watchlists',
            field=models.ManyToManyField(through='core.UserHasWatchlist', to='core.Watchlist'),
        ),
        migrations.CreateModel(
            name='Session',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('token', models.CharField(max_length=1000)),
                ('os', models.CharField(max_length=255, null=True)),
                ('os_version', models.CharField(max_length=255, null=True)),
                ('browser', models.CharField(max_length=255, null=True)),
                ('browser_version', models.CharField(max_length=255, null=True)),
                ('device', models.CharField(max_length=255, null=True)),
                ('expiration_date', models.DateTimeField()),
                ('active', models.BooleanField(default=True)),
                ('user', models.ForeignKey(db_column='user_id', on_delete=django.db.models.deletion.CASCADE, related_name='+', to='core.User')),
            ],
            options={
                'db_table': 'session',
            },
        ),
        migrations.CreateModel(
            name='Picture',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('url', models.CharField(max_length=5000)),
                ('category', models.ForeignKey(db_column='picture_category', on_delete=django.db.models.deletion.CASCADE, related_name='+', to='core.PictureCategory')),
                ('movie', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='pictures', to='core.Movie')),
            ],
            options={
                'db_table': 'picture',
            },
        ),
        migrations.CreateModel(
            name='Crew',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=500)),
                ('character_name', models.CharField(max_length=500)),
                ('picture_url', models.CharField(max_length=5000, null=True)),
                ('category', models.ForeignKey(db_column='crew_category_id', on_delete=django.db.models.deletion.CASCADE, related_name='+', to='core.CrewCategory')),
                ('movie', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='crew', to='core.Movie')),
            ],
            options={
                'db_table': 'crew',
            },
        ),
        migrations.CreateModel(
            name='Credential',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('token', models.CharField(max_length=1000)),
                ('auth_provider', models.ForeignKey(db_column='auth_provider_id', on_delete=django.db.models.deletion.CASCADE, related_name='+', to='core.AuthProvider')),
                ('user', models.ForeignKey(db_column='user_id', on_delete=django.db.models.deletion.CASCADE, related_name='+', to='core.User')),
            ],
            options={
                'db_table': 'credential',
            },
        ),
    ]
