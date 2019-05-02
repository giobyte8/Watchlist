from django.test import TestCase

from core.models import User, Role, WatchlistPermission


class TestWatchlistsCrud(TestCase):

    @classmethod
    def setUpTestData(cls):
        super().setUpTestData()
        WatchlistPermission.objects.create(name='Owner')
        role = Role.objects.create(name='Watcher')
        cls.user = User.objects.create(
            picture="",
            name="Test user",
            email="test.user@sample.com",
            role=role
        )

    def test_list_creation(self):
        url = '/api/user/' + str(self.user.id) + '/lists'

        response = self.client.post(url, {"name": "Test list"})
        self.assertEqual(response.status_code, 200)

    def test_list_create_duplicated(self):
        url = '/api/user/' + str(self.user.id) + '/lists'

        # Create a list and retrieve its id
        response = self.client.post(url, {"name": "Test"})
        self.assertEqual(response.status_code, 200)
        watchlist = response.json()
        list_id = watchlist['watchlist']['id']

        # Create again a list with same name and check that
        # same list is returned instead of create a new one
        response = self.client.post(url, {"name": "Test"})
        self.assertEqual(response.status_code, 200)
        watchlist = response.json()
        success = watchlist['success']
        list2_id = watchlist['watchlist']['id']
        self.assertEqual(success, False)
        self.assertEqual(list2_id, list_id)

    def test_list_create_same_name_different_user(self):
        url = '/api/user/' + str(self.user.id) + '/lists'
        user2 = User.objects.create(
            picture="",
            name="Test user 2",
            email="test2.user@sample.com",
            role_id=1
        )

        # Create a list for user 1 and retrieve its id
        response = self.client.post(url, {"name": "Test"})
        self.assertEqual(response.status_code, 200)
        watchlist = response.json()
        list_id = watchlist['watchlist']['id']

        # Create list with same name for the new user
        url_user2 = '/api/user/' + str(user2.id) + '/lists'
        response = self.client.post(url_user2, {"name": "Test"})
        self.assertEqual(response.status_code, 200)

        # Verify second list creation
        list_post_response = response.json()
        success = list_post_response['success']
        list2_id = list_post_response['watchlist']['id']
        self.assertEqual(success, True)
        self.assertNotEqual(list_id, list2_id)

    def test_lists_fetch(self):
        url = '/api/user/' + str(self.user.id) + '/lists'
        response = self.client.post(url, {"name": "Test list"})
        self.assertEqual(response.status_code, 200)

        response = self.client.get(url)
        self.assertEqual(response.status_code, 200)
        j_lists = response.json()
        self.assertEqual(len(j_lists), 1)

        response = self.client.post(url, {"name": "Test list 2"})
        self.assertEqual(response.status_code, 200)

        response = self.client.get(url)
        self.assertEqual(response.status_code, 200)
        j_lists = response.json()
        self.assertEqual(len(j_lists), 2)
