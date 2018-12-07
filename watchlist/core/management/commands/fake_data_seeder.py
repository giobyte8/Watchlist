from django.core.management.base import BaseCommand
from django.db import transaction
from faker import Faker

from core.management.fake_providers.watchlist_name_provider import Provider
from core.models import User, Watchlist


# noinspection PyMethodMayBeStatic
class Command(BaseCommand):
    fake = Faker()

    def handle(self, *args, **options):
        with transaction.atomic():
            self.seed_users()
            self.seed_watch_lists()

    def seed_users(self):
        admin = User()
        admin.name = self.fake.name()
        admin.role_id = 1
        admin.picture = self.fake.image_url()
        admin.save()

        for i in range(4):
            watcher = User()
            watcher.name = self.fake.name()
            watcher.role_id = 2
            watcher.picture = self.fake.image_url()
            watcher.save()

    def seed_watch_lists(self):
        self.fake.add_provider(Provider)

        watchers = User.objects.filter(role_id=2)
        for watcher in watchers:
            self.__make_watchlist(watcher.id, True)

            for i in range(4):
                self.__make_watchlist(watcher.id)

    def __make_watchlist(self, user_id, is_default=False):
        watchlist = Watchlist()
        watchlist.name = self.fake.watchlist_name()
        watchlist.is_default_list = is_default
        watchlist.created_by_id = user_id
        watchlist.save()
