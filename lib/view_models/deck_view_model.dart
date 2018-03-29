import '../models/deck.dart';

import 'stream_demuxer.dart';
import 'persistent_stream.dart';

class DeckViewModel {
  final String key;
  final String name;
  final String access;
  final int cardsToLearn;

  DeckViewModel({this.key, this.name, this.access, this.cardsToLearn});
}

class DecksViewModel {
  final Iterable<Deck> deckModels;
  final List<PersistentStream<DeckViewModel>> decks;

  DecksViewModel(this.deckModels)
      : decks = deckModels
            .map((deck) => new PersistentStream(() => new StreamDemuxer({
                  'access': deck.getAccess(),
                  'cardsToLearn': deck.getNumberOfCardsToLearn(),
                }).map((data) => new DeckViewModel(
                    key: deck.key,
                    name: deck.name,
                    access: data['access'],
                    cardsToLearn: data['cardsToLearn']))))
            .toList();

  static PersistentStream<DecksViewModel> getDecks(String uid) {
    return new PersistentStream(() => Deck.getDecks(uid).map((decks) {
          return new DecksViewModel(decks);
        }));
  }
}
