from django.test import TestCase

from core.models import User


class ApiTest(TestCase):

    @classmethod
    def setUpTestData(cls):
        super().setUpTestData()
        cls.user = User.objects.create(
            picture="",
            name="Test user",
            email="test.user@sample.com",
            role_id=2
        )

    # Test list creation
    # Test list validation for duplicity
    # Test allow create list with same name by different user

    # Test fetch of lists for specific user (Validate count)
    # Test result of fetch for undefined user

    # Test add a movie to list
    # Test that add same movie multiple times does not duplicates
    # Test that add same movie to another list does not duplicates movie

    # Test fetch movies of list (Validate count)
    # Test fetch movies of undefined list
