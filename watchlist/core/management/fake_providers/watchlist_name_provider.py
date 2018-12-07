from faker.providers import BaseProvider
from random import SystemRandom


class Provider(BaseProvider):
    names = [
        'For kids',
        'Animated',
        'Top SciFi'
        'Action',
        'Spies collection',
        'Romance',
        'Animation',
        'For the girls',
        'Grandfathers time',
        'Parents time',
        'Cousins time',
        'Brothers time',
        'Couple time',
        'Chill',
        'Sunday',
        'Corn Pop'
        'Western classics',
        'Action',
        'Biographies',
        'Tip inspiration',
        'Smart people'
    ]

    def watchlist_name(self):
        sec_random = SystemRandom()
        return sec_random.choice(Provider.names)
