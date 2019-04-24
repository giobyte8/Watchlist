# Generated by Django 2.2 on 2019-04-16 04:12

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('core', '0001_initial'),
    ]

    operations = [
        migrations.CreateModel(
            name='AuthProvider',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=255)),
            ],
            options={
                'db_table': 'auth_provider',
            },
        ),
        migrations.AlterField(
            model_name='watchlisthasmovie',
            name='watchlist',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='has_movies', to='core.Watchlist'),
        ),
        migrations.CreateModel(
            name='Credential',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('token', models.CharField(max_length=1000)),
                ('auth_provider', models.ForeignKey(db_column='auth_provider_id', on_delete=django.db.models.deletion.CASCADE, related_name='+', to='core.AuthProvider')),
                ('user', models.ForeignKey(db_column='user_id', on_delete=django.db.models.deletion.CASCADE, related_name='+', to='core.User')),
            ],
        ),
    ]
