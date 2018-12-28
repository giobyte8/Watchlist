from rest_framework.response import Response
from rest_framework.views import APIView

from api.serializer import WatchlistSerializer, WatchlistContentSerializer
from core.models import Watchlist


class Watchlists(APIView):

    def get(self, request):
        lists = Watchlist.objects.all()
        serializer = WatchlistSerializer(lists, many=True)
        return Response(serializer.data)


class WatchlistContent(APIView):
    def get(self, request, watchlist_id):
        watchlist = Watchlist.objects.get(pk=watchlist_id)
        serializer = WatchlistContentSerializer(watchlist)
        return Response(serializer.data)

