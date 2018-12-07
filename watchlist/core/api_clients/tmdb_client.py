import requests

from core.models import Movie
from datetime import datetime


class TMDBClient:

    def __init__(self):
        self.api_key = 'b05e87f356ef223c5aeacf0bcae54d04'
        self.api_base_bath = 'https://api.themoviedb.org/3/'

    def random_movies(self):
        movies = []
        for i in range(5, 7):
            movies += self.popular_movies(i)

        return movies

    def popular_movies(self, page=1):
        url = self.api_base_bath + 'movie/popular'
        payload = {'api_key': self.api_key, 'page': page}
        popular_movies = requests.get(url, payload).json()['results']

        movies = []
        for movie in popular_movies:
            movies.append(self.movie_details(movie['id']))

        return movies

    def movie_details(self, movie_id):
        url = self.api_base_bath + 'movie/' + str(movie_id)
        payload = {'api_key': self.api_key}
        j_movie = requests.get(url, payload).json()

        movie = Movie()
        movie.tmdb_id = j_movie['id']
        movie.title = j_movie['title']
        movie.original_title = j_movie['original_title']
        movie.release_date = datetime.strptime(
            j_movie['release_date'],
            '%Y-%m-%d'
        )
        movie.runtime = j_movie['runtime']
        movie.synopsis = j_movie['overview']
        movie.rating = j_movie['vote_average']
        return movie

    def random_movie(self):
        movies = self.random_movies()
        for movie in movies:
            print(movie.title)

        return ''
