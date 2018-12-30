#!/usr/bin/env python

import json
import os
import subprocess
import sys
import uuid

# TODO(dotdoom): add argparse for these.
project = os.environ.get('PROJECT', 'delern-debug')
number_of_decks = 2
cards_per_deck = 5000

if len(sys.argv) != 2:
  print('Usage: %s <user_id_to_create_decks_for>' % sys.argv[0])
  sys.exit(1)
uid = sys.argv[1]

database = json.load(subprocess.Popen([
  'firebase', '--project', project, 'database:get', '/',
], stdout=subprocess.PIPE).stdout)

# Create /decks
database.setdefault('deck_access', {})

database.setdefault('decks', {})
database['decks'].setdefault(uid, {})

database.setdefault('learning', {})
database['learning'].setdefault(uid, {})

for deck_number in xrange(number_of_decks):
  deck_key = 'fake-deck-' + str(uuid.uuid1())
  database['decks'][uid][deck_key] = {
      'name': 'Fake Deck %d' % (deck_number+1),
      'markdown': True,
  }
  database['deck_access'][deck_key] = {
      uid: {
          'access': 'owner',
      },
  }
  cards = {}
  scheduled_cards = {}
  for card_number in xrange(cards_per_deck):
    card_key = 'fake-card-' + str(uuid.uuid1())
    cards[card_key] = {
        'front': 'Fake Card %d. Flip for keys!' % (card_number+1),
        'back': (
          '```\n'
          'Deck number: %d\n'
          'Deck key: %s\n'
          'Card key: %s\n'
          '```' % (deck_number+1, deck_key, card_key)),
    }
    scheduled_cards[card_key] = {
        'level': 'L0',
        'repeatAt': 0,
    }
  database['cards'][deck_key] = cards
  database['learning'][uid][deck_key] = scheduled_cards

print('NOTE: deleting all trigger functions to speed up import!\n'
      'This operation will fail if functions are already deleted. This'
      'is okay.\n'
      'Do not forget to re-deploy the functions afterwards!')
subprocess.call([
  'firebase', '--project', project, 'functions:delete', 'triggers',
])

update_process = subprocess.Popen([
  'firebase', '--project', project, 'database:set', '--confirm', '/',
], stdin=subprocess.PIPE)
json.dump(database, update_process.stdin)
update_process.communicate()
