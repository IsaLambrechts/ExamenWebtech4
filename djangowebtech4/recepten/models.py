from django.db import models


class Recept(models.Model):
    naam = models.CharField(max_length=200)
    aantal_calorien = models.IntegerField()
    benodigde_tijd = models.IntegerField()

    def __str__(self):
        return self.naam


class Ingredient(models.Model):
    recept = models.ForeignKey(Recept, on_delete=models.CASCADE)



