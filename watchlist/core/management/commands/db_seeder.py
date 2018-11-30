from django.core.management.base import BaseCommand
from core.models import Role


class Command(BaseCommand):
    def handle(self, *args, **options):
        self.seed_roles()
        self.stdout.write(self.style.SUCCESS(
            'Database seeded successfully'))

    @staticmethod
    def seed_roles():
        admin = Role()
        admin.name = 'admin'
        admin.save()

        audience = Role()
        audience.name = 'watcher'
        audience.save()

