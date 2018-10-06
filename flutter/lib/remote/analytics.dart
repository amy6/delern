import 'dart:async';

import 'package:firebase_analytics/firebase_analytics.dart';

Future<void> logDeckCreate() =>
    FirebaseAnalytics().logEvent(name: 'deck_create');

Future<void> logDeckDelete(String deckId) =>
    FirebaseAnalytics().logEvent(name: 'deck_delete', parameters: {
      'item_id': deckId,
    });

Future<void> logStartLearning(String deckId) =>
    FirebaseAnalytics().logEvent(name: 'deck_learning_start', parameters: {
      'item_id': deckId,
    });

Future<void> logShare(String deckId) => FirebaseAnalytics()
    .logShare(contentType: 'application/flashcards-deck', itemId: deckId);
