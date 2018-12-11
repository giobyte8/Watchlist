from django.core.management.base import BaseCommand

from core.api_clients.tmdb_client import TMDBClient
from core.models import Movie, Genre


class Command(BaseCommand):
    tmdb_client = TMDBClient()

    def handle(self, *args, **options):
        j_movies = self.tmdb_client.random_movies()
        for j_movie in j_movies:
            movie = Movie.from_json(j_movie)
            count = Movie.objects.filter(tmdb_id=movie.tmdb_id).count()
            if count == 0:
                movie.save()
                self.process_genres(j_movie, movie)

        self.stdout.write(self.style.SUCCESS(
            "Movies seeded"
        ))

    @staticmethod
    def process_genres(j_movie, movie):
        for j_genre in j_movie['genres']:
            genre = Genre.from_json(j_genre)

            count = Genre.objects.filter(id=genre.id).count()
            if count == 0:
                genre.save()
                movie.genres.add(genre)
            else:
                genre = Genre.objects.filter(id=genre.id).first()
                movie.genres.add(genre)
