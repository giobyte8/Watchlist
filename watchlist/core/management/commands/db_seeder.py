from django.core.management.base import BaseCommand
from core.models import Role, WatchlistPermission, CrewCategory, PictureCategory, AuthProvider


class Command(BaseCommand):
    def handle(self, *args, **options):
        self.seed_roles()
        self.seed_auth_providers()
        self.seed_watchlist_permissions()
        self.seed_crew_categories()
        self.seed_picture_categories()

    @staticmethod
    def seed_roles():
        admin = Role()
        admin.name = 'admin'
        admin.save()

        audience = Role()
        audience.name = 'watcher'
        audience.save()

    def seed_auth_providers(self):
        fb = AuthProvider()
        fb.name = "Facebook"
        fb.save()

        google = AuthProvider()
        google.name = "Google"
        google.save()

    @staticmethod
    def seed_watchlist_permissions():
        permission = WatchlistPermission()
        permission.name = 'Owner'
        permission.save()

        permission = WatchlistPermission()
        permission.name = 'Edition'
        permission.save()

        permission = WatchlistPermission()
        permission.name = 'View'
        permission.save()

    @staticmethod
    def seed_crew_categories():
        category = CrewCategory()
        category.name = 'Director'
        category.save()

        category = CrewCategory()
        category.name = 'Cast'
        category.save()

    @staticmethod
    def seed_picture_categories():
        category = PictureCategory()
        category.name = 'Poster'
        category.save()

        category = PictureCategory()
        category.name = 'Backdrop'
        category.save()

