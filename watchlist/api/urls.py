from django.urls import path
from api import views

urlpatterns = [
    path('lists', views.Watchlists.as_view()),
    path('lists/<int:watchlist_id>', views.WatchlistContent.as_view()),
]
