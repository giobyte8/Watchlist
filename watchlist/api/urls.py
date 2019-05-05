from django.urls import path
from api import views

urlpatterns = [
    path('login', views.Auth.as_view()),
    path('user/<int:user_id>/lists', views.Watchlists.as_view()),
    path('lists/<int:watchlist_id>/movies', views.WatchlistMovies.as_view()),
]
