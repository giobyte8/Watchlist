from rest_framework.response import Response
from rest_framework.views import APIView

from api.serializer import WatchlistSerializer, WatchlistContentSerializer, WatchlistHasMovieSerializer
from core.models import Watchlist, User


class Watchlists(APIView):

    def get(self, request, user_id):
        lists = Watchlist.objects.filter(user__pk=user_id)
        serializer = WatchlistSerializer(lists, many=True)
        return Response(serializer.data)


class WatchlistMovies(APIView):

    def get(self, request, watchlist_id):
        watchlist = Watchlist.objects.get(pk=watchlist_id)
        for has_movie in watchlist.has_movies.all():
            print(has_movie.movie.title)

        serializer = WatchlistHasMovieSerializer(
            watchlist.has_movies,
            many=True
        )
        # serializer = WatchlistContentSerializer(watchlist)
        return Response(serializer.data)

