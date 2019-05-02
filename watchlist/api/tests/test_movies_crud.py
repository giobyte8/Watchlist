from django.test import TestCase

from core.models import WatchlistPermission, Role, User, Watchlist, UserHasWatchlist, PictureCategory, \
    CrewCategory


class TestMoviesCrud(TestCase):

    @classmethod
    def setUpTestData(cls):
        super().setUpTestData()
        CrewCategory.objects.create(name='Director')
        CrewCategory.objects.create(name='Cast')
        PictureCategory.objects.create(name='Poster')
        PictureCategory.objects.create(name='Backdrop')
        WatchlistPermission.objects.create(name='Owner')
        role = Role.objects.create(name='Watcher')
        cls.user = User.objects.create(
            picture="",
            name="Test user",
            email="test.user@sample.com",
            role=role
        )

        cls.list = Watchlist.objects.create(name="Test list")
        UserHasWatchlist.objects.create(
            user=cls.user,
            watchlist=cls.list,
            permission_id=1
        )

        cls.list2 = Watchlist.objects.create(name="Test list 2")
        UserHasWatchlist.objects.create(
            user=cls.user,
            watchlist=cls.list2,
            permission_id=1
        )

    def test_add_movie(self):
        url = '/api/lists/' + str(self.list.id) + '/movies'
        response = self.client.post(url, {
            "tmdb_id": 550,
            "added_by": self.user.id
        })
        self.assertEqual(response.status_code, 200)

    def test_duplicate_movie(self):
        url = '/api/lists/' + str(self.list.id) + '/movies'

        # Create movie (First time)
        response = self.client.post(url, {
            "tmdb_id": 550,
            "added_by": self.user.id
        })
        self.assertEqual(response.status_code, 200)
        j_has_movie = response.json()
        has_movie_id = j_has_movie['id']

        # Post same movie
        response = self.client.post(url, {
            "tmdb_id": 550,
            "added_by": self.user.id
        })
        self.assertEqual(response.status_code, 200)
        j_has_movie = response.json()
        has_movie_id_2 = j_has_movie['id']

        # Verify duplicity prevention
        self.assertEqual(has_movie_id, has_movie_id_2)

    def test_duplicate_movie_different_list(self):
        url_list1 = '/api/lists/' + str(self.list.id) + '/movies'
        url_list2 = '/api/lists/' + str(self.list2.id) + '/movies'

        # Create movie on list 1
        response = self.client.post(url_list1, {
            "tmdb_id": 550,
            "added_by": self.user.id
        })
        self.assertEqual(response.status_code, 200)
        j_has_movie = response.json()
        has_movie_id = j_has_movie['id']
        movie_id = j_has_movie['movie']['id']

        # Post same movie on list 2
        response = self.client.post(url_list2, {
            "tmdb_id": 550,
            "added_by": self.user.id
        })
        self.assertEqual(response.status_code, 200)
        j_has_movie = response.json()
        has_movie_id_2 = j_has_movie['id']
        movie_id_2 = j_has_movie['movie']['id']

        # Verify different 'has_movie' but same 'movie'
        self.assertNotEqual(has_movie_id, has_movie_id_2)
        self.assertEqual(movie_id, movie_id_2)

    def test_fetch_movies(self):
        url = '/api/lists/' + str(self.list.id) + '/movies'
        response = self.client.post(url, {
            "tmdb_id": 550,
            "added_by": self.user.id
        })
        self.assertEqual(response.status_code, 200)

        response = self.client.get(url)
        self.assertEqual(response.status_code, 200)
        j_movies = response.json()
        self.assertEqual(len(j_movies), 1)
