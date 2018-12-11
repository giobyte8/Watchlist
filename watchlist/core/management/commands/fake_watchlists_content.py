from django.core.management.base import BaseCommand

from core.api_clients.tmdb_client import TMDBClient


class Command(BaseCommand):
    tmdb_client = TMDBClient()

    def handle(self, *args, **options):
        movies = self.tmdb_client.random_movies()
        for movie in movies:
            # print(movie.original_title)
            movie.save()

        self.stdout.write(self.style.SUCCESS(
            "Movies seeded"
        ))


