import jwt
from jwt import ExpiredSignatureError, DecodeError
from rest_framework.authentication import BaseAuthentication

from core.models import User


class APIWatchlistAuthentication(BaseAuthentication):

    # noinspection PyUnusedLocal
    def authenticate(self, request):
        if 'Authorization' in request.headers:
            token = request.headers['Authorization'].split(' ')[1]

            try:
                payload = jwt.decode(token, 'secret', algorithms='HS256')
                user_id = payload['user_id']
                user = User.objects.filter(id=user_id).first()

                return user, token,
            except (ExpiredSignatureError, DecodeError,) as e:
                pass

        return None
