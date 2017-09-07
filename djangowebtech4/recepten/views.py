from django.shortcuts import render
from .models import Recept


def index(request):
    recepten = Recept.objects.all()
    return render(request, 'recepten/index.html', {"recepten": recepten})
