import requests


class FBClient:
    def __init__(self):
        self._base_url = 'https://graph.facebook.com/'

    def verify_token(self, email, token):
        url = self._base_url + 'me'
        payload = {
            'fields': 'id,email',
            'access_token': token
        }

        response = requests.get(url, payload).json()
        if 'error' in response:
            return False

        r_email = response['email']
        return r_email == email
