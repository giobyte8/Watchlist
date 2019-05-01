from rest_framework import serializers
from core.models import Watchlist, WatchlistHasMovie, Movie, Genre, Picture, PictureCategory


class GenreSerializer(serializers.ModelSerializer):
    class Meta:
        model = Genre
        fields = (
            'id',
            'name'
        )


class WatchlistSerializer(serializers.ModelSerializer):
    class Meta:
        model = Watchlist
        fields = (
            'id',
            'name',
            'is_default_list',
            'created_at',
            'updated_at',
            'deleted'
        )


class PictureCategorySerializer(serializers.ModelSerializer):
    class Meta:
        model = PictureCategory
        fields = (
            'id',
            'name'
        )


class PictureSerializer(serializers.ModelSerializer):
    category = PictureCategorySerializer()

    class Meta:
        model = Picture
        fields = (
            'id',
            'url',
            'category'
        )


class MovieSerializer(serializers.ModelSerializer):
    genres = GenreSerializer(many=True)
    pictures = PictureSerializer(many=True)

    class Meta:
        model = Movie
        fields = (
            'id',
            'tmdb_id',
            'title',
            'original_title',
            'synopsis',
            'rating',
            'genres',
            'pictures'
        )


class WatchlistHasMovieSerializer(serializers.ModelSerializer):
    movie = MovieSerializer()

    class Meta:
        model = WatchlistHasMovie
        fields = ('id', 'added_at', 'seen_at', 'added_by', 'movie')


class WatchlistContentSerializer(serializers.ModelSerializer):
    has_movies = WatchlistHasMovieSerializer(many=True)

    class Meta:
        model = Watchlist
        fields = ('id', 'name', 'is_default_list', 'has_movies')


class WatchlistPostResponseSerializer(serializers.Serializer):
    success = serializers.BooleanField()
    message = serializers.CharField()
    watchlist = WatchlistSerializer()

    def update(self, instance, validated_data):
        pass

    def create(self, validated_data):
        pass
