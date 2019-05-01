from django.urls import path
from api import views

urlpatterns = [
    path('user/<int:user_id>/lists', views.Watchlists.as_view()),
    path('lists/<int:watchlist_id>/movies', views.WatchlistMovies.as_view()),
]
