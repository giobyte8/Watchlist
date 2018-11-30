from django.core.management.base import BaseCommand
from django_seed import Seed

from core.models import User, Role


class Command(BaseCommand):
    def handle(self, *args, **options):
        seeder = Seed.seeder()
        seeder.add_entity(User, 1, {
            'name': lambda x: seeder.faker.name(),
            'role': lambda x: Role.objects.first(),
            'picture': lambda x: seeder.faker.image_url()
        })
        seeder.add_entity(User, 150000, {
            'name': lambda x: seeder.faker.name(),
            'role': lambda x: Role.objects.last(),
            'picture': lambda x: seeder.faker.image_url()
        })

        seeder.execute()


