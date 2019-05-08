from rest_framework import status
from rest_framework.response import Response
from rest_framework.views import APIView

from api.serializer import WatchlistSerializer, WatchlistHasMovieSerializer, WatchlistPostResponseSerializer, \
    LoginResponseSerializer
from core.api_clients.fb_client import FBClient
from core.api_clients.tmdb_client import TMDBClient
from core.models import Watchlist, WatchlistHasMovie, Movie, Genre, Crew, Picture, UserHasWatchlist, User, Credential


class Auth(APIView):
    _fb_client = FBClient()

    def post(self, request):
        name = request.data['name']
        email = request.data['email']
        picture = request.data['picture']
        token = request.data['token']
        auth_provider_id = request.data['auth_provider_id']
        response = {'success': False}

        if auth_provider_id == 1:
            if self._fb_client.verify_token(email, token):
                response['success'] = True
                response['message'] = 'Login successful'
                response['user'] = self._upsert_user(
                    name,
                    email,
                    picture,
                    token,
                    1
                )
            else:
                response['message'] = 'Facebook auth token verification has failed'
                response['user'] = None
        else:
            response['message'] = 'Unsupported auth provider'
            response['user'] = None

        serializer = LoginResponseSerializer(response)
        return Response(serializer.data)

    def _upsert_user(self, name, email, picture, token, auth_provider_id):
        user = User.objects.filter(email=email).first()
        if user is None:
            user = User()

        user.picture = picture
        user.name = name
        user.email = email
        user.role_id = 2
        user.save()

        credential = Credential.objects.filter(
            auth_provider_id=auth_provider_id,
            user_id=user.id
        ).first()
        if credential is None:
            credential = Credential()

        credential.token = token
        credential.auth_provider_id = auth_provider_id
        credential.user_id = user.id
        credential.save()

        return user


class Watchlists(APIView):

    # noinspection PyUnusedLocal
    def get(self, request, user_id):
        lists = Watchlist.objects.filter(user__pk=user_id)
        serializer = WatchlistSerializer(lists, many=True)
        return Response(serializer.data)

    def post(self, request, user_id):
        serializer = WatchlistSerializer(data=request.data)
        if serializer.is_valid():

            # Another list with same name?
            previous_list = Watchlist.objects.\
                filter(
                    name=serializer.validated_data.get('name'),
                    user__id=user_id
                )\
                .first()
            if previous_list is not None:
                list_post_response = {
                    "success": False,
                    "message": "A list with same name is already registered",
                    "watchlist": previous_list
                }
                response_serializer = WatchlistPostResponseSerializer(list_post_response)
                return Response(response_serializer.data)

            watchlist = serializer.save()
            UserHasWatchlist.objects.create(
                user_id=user_id,
                watchlist=watchlist,
                permission_id=1
            )

            list_post_response = {
                "success": True,
                "message": "",
                "watchlist": watchlist
            }
            response_serializer = WatchlistPostResponseSerializer(list_post_response)
            return Response(response_serializer.data)
        else:
            return Response(status=status.HTTP_400_BAD_REQUEST)


class WatchlistMovies(APIView):
    _tmdb_client = TMDBClient()

    # noinspection PyUnusedLocal
    def get(self, request, watchlist_id):
        has_movies = WatchlistHasMovie.objects.filter(watchlist_id=watchlist_id)
        serializer = WatchlistHasMovieSerializer(
            has_movies,
            many=True
        )
        return Response(serializer.data)

    def post(self, request, watchlist_id):
        tmdb_id = request.data['tmdb_id']
        added_by = request.data['added_by']

        # Previously added?
        previously_added = WatchlistHasMovie.objects\
            .filter(movie__tmdb_id=tmdb_id, watchlist_id=watchlist_id)\
            .first()
        if previously_added is not None:
            serializer = WatchlistHasMovieSerializer(previously_added)
            return Response(serializer.data)

        movie = self._upsert_movie(tmdb_id)
        if movie is not None:
            has_movie = WatchlistHasMovie.objects.create(
                watchlist_id=watchlist_id,
                movie=movie,
                added_by_id=added_by
            )
            serializer = WatchlistHasMovieSerializer(has_movie)
            return Response(serializer.data)
        else:
            return Response(status=status.HTTP_400_BAD_REQUEST)

    def _upsert_movie(self, tmdb_id):
        movie = Movie.objects.filter(tmdb_id=tmdb_id).first()
        if movie is None:
            j_movie = self._tmdb_client.movie_details(tmdb_id)
            if j_movie is None:
                return None

            movie = Movie.from_json(j_movie)
            movie.save()

            # Store genres
            for j_genre in j_movie['genres']:
                genre = Genre.from_json(j_genre)

                count = Genre.objects.filter(id=genre.id).count()
                if count == 0:
                    genre.save()
                    movie.genres.add(genre)
                else:
                    genre = Genre.objects.filter(id=genre.id).first()
                    movie.genres.add(genre)

            # Process cast
            for j_cast in j_movie['credits']['cast']:
                crew = Crew()
                crew.movie_id = movie.id
                crew.category_id = 2
                crew.name = j_cast['name']
                crew.character_name = j_cast['character']
                crew.picture_url = j_cast['profile_path']
                crew.save()

            # Process directors
            for j_crew in j_movie['credits']['crew']:
                if j_crew['job'] == 'Director':
                    crew = Crew()
                    crew.movie_id = movie.id
                    crew.category_id = 1
                    crew.name = j_crew['name']
                    crew.picture_url = j_crew['profile_path']
                    crew.save()

            # Process pictures
            if 'backdrop_path' in j_movie and j_movie['backdrop_path'] is not None:
                picture = Picture()
                picture.category_id = 2
                picture.movie_id = movie.id
                picture.url = j_movie['backdrop_path']
                picture.save()
            if 'poster_path' in j_movie and j_movie['poster_path'] is not None:
                picture = Picture()
                picture.category_id = 1
                picture.movie_id = movie.id
                picture.url = j_movie['poster_path']
                picture.save()

        return movie
