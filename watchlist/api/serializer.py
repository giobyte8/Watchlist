from rest_framework import serializers
from core.models import Watchlist, WatchlistHasMovie, Movie


class WatchlistSerializer(serializers.ModelSerializer):
    class Meta:
        model = Watchlist
        fields = ('id', 'name', 'is_default_list')


class MovieSerializer(serializers.ModelSerializer):
    class Meta:
        model = Movie
        fields = ('id', 'title')


class WatchlistHasMovieSerializer(serializers.ModelSerializer):
    movie = MovieSerializer()

    class Meta:
        model = WatchlistHasMovie
        fields = ('id', 'added_at', 'seen_at', 'movie')


class WatchlistContentSerializer(serializers.ModelSerializer):
    has_movies = WatchlistHasMovieSerializer(many=True)

    class Meta:
        model = Watchlist
        fields = ('id', 'name', 'is_default_list', 'has_movies')
