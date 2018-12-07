#!/usr/bin/env bash

# Redo migrations
python manage.py migrate core zero
python manage.py migrate core

# Apply seeders
python manage.py db_seeder
python manage.py fake_data_seeder
