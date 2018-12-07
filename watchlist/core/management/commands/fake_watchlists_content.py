from django.core.management.base import BaseCommand

from core.api_clients.tmdb_client import TMDBClient


class Command(BaseCommand):
    tmdb_client = TMDBClient()

    def handle(self, *args, **options):
        self.stdout.write(self.style.SUCCESS(
            self.tmdb_client.random_movie()
        ))


