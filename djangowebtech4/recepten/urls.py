from django.conf.urls import url

from . import views

name = "recepten"
urlpatterns = [
    url(r'^$', views.index, name='index'),
]