/*
 * Copyright (C) 2017 Katarina Sheremet
 * This file is part of Delern.
 *
 * Delern is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Delern is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with  Delern.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.dasfoo.delern.di;

import org.dasfoo.delern.addupdatecard.IAddEditCardView;
import org.dasfoo.delern.di.components.AddCardActivityComponent;
import org.dasfoo.delern.di.components.DaggerAddCardActivityComponent;
import org.dasfoo.delern.di.components.DaggerDelernMainActivityComponent;
import org.dasfoo.delern.di.components.DaggerEditCardListActivityComponent;
import org.dasfoo.delern.di.components.DaggerEditDeckActivityComponent;
import org.dasfoo.delern.di.components.DaggerLearningCardsActivityComponent;
import org.dasfoo.delern.di.components.DaggerPreEditCardActivityComponent;
import org.dasfoo.delern.di.components.DaggerShareDeckActivityComponent;
import org.dasfoo.delern.di.components.DaggerUpdateCardActivityComponent;
import org.dasfoo.delern.di.components.DelernMainActivityComponent;
import org.dasfoo.delern.di.components.EditCardListActivityComponent;
import org.dasfoo.delern.di.components.EditDeckActivityComponent;
import org.dasfoo.delern.di.components.LearningCardsActivityComponent;
import org.dasfoo.delern.di.components.PreEditCardActivityComponent;
import org.dasfoo.delern.di.components.ShareDeckActivityComponent;
import org.dasfoo.delern.di.components.UpdateCardActivityComponent;
import org.dasfoo.delern.di.modules.AddCardActivityModule;
import org.dasfoo.delern.di.modules.DelernMainActivityModule;
import org.dasfoo.delern.di.modules.EditCardListActivityModule;
import org.dasfoo.delern.di.modules.EditDeckActivityModule;
import org.dasfoo.delern.di.modules.LearningCardsActivityModule;
import org.dasfoo.delern.di.modules.PreEditCardActivityModule;
import org.dasfoo.delern.di.modules.ShareDeckActivityModule;
import org.dasfoo.delern.di.modules.UpdateCardActivityModule;
import org.dasfoo.delern.editdeck.IEditDeckView;
import org.dasfoo.delern.learncards.ILearningCardsView;
import org.dasfoo.delern.listdecks.IDelernMainView;
import org.dasfoo.delern.models.Card;
import org.dasfoo.delern.models.Deck;
import org.dasfoo.delern.models.DeckAccess;
import org.dasfoo.delern.previewcard.IPreEditCardView;

/**
 * Initialize components for creating class.
 */
@SuppressWarnings(/* normal for dependency injectors? */ "checkstyle:classFanOutComplexity")
public final class Injector {

    private Injector() {
        // Private constructor to prohibit to create instance of class
    }

    /**
     * Method returns injector class.
     *
     * @param view view to init Presenter for callbacks.
     * @return DelernMainActivityComponent.
     */
    public static DelernMainActivityComponent getMainActivityInjector(final IDelernMainView view) {
        return DaggerDelernMainActivityComponent
                .builder()
                .delernMainActivityModule(new DelernMainActivityModule(view)).build();
    }

    /**
     * Method returns injector class.
     *
     * @param view view to init Presenter for callbacks.
     * @param deck deck to init Presenter.
     * @return AddEditCardActivityComponent.
     */
    public static AddCardActivityComponent getAddActivityInjector(
            final IAddEditCardView view, final Deck deck) {
        return DaggerAddCardActivityComponent
                .builder()
                .addCardActivityModule(new AddCardActivityModule(view, deck)).build();
    }

    /**
     * Method returns injector class.
     *
     * @param view view to init Presenter for callbacks.
     * @param card card to init Presenter.
     * @return AddEditCardActivityComponent.
     */
    public static UpdateCardActivityComponent getUpdateActivityInjector(
            final IAddEditCardView view, final Card card) {
        return DaggerUpdateCardActivityComponent
                .builder()
                .updateCardActivityModule(new UpdateCardActivityModule(view, card)).build();
    }

    /**
     * Method returns injector class.
     *
     * @param deck deck to inject.
     * @return EditCardListActivityComponent.
     */
    public static EditCardListActivityComponent getEditCardListActivityInjector(final Deck deck) {
        return DaggerEditCardListActivityComponent
                .builder()
                .editCardListActivityModule(new EditCardListActivityModule(deck)).build();
    }

    /**
     * Method returns injector class.
     *
     * @param view view to init Presenter for callbacks.
     * @return LearningCardsActivityComponent.
     */
    public static LearningCardsActivityComponent getLearningCardsActivityInjector(
            final ILearningCardsView view) {
        return DaggerLearningCardsActivityComponent
                .builder()
                .learningCardsActivityModule(new LearningCardsActivityModule(view)).build();
    }

    /**
     * Method returns injector class.
     *
     * @param view view to init Presenter for callbacks.
     * @return PreEditCardActivityComponent.
     */
    public static PreEditCardActivityComponent getPreEditCardActivityInjector(
            final IPreEditCardView view) {
        return DaggerPreEditCardActivityComponent
                .builder()
                .preEditCardActivityModule(new PreEditCardActivityModule(view)).build();
    }

    /**
     * Method returns injector class.
     *
     * @param view       view to init Presenter of callbacks.
     * @param deckAccess deckAccess to change settings.
     * @return EditDeckActivityComponent.
     */
    public static EditDeckActivityComponent getEditDeckActivityInjector(final IEditDeckView view,
                                                                        final DeckAccess
                                                                                deckAccess) {
        return DaggerEditDeckActivityComponent
                .builder()
                .editDeckActivityModule(new EditDeckActivityModule(view, deckAccess)).build();
    }

    /**
     * Method returns injector class.
     *
     * @param deck deck to perform sharing.
     * @return ShareDeckActivityComponent.
     */
    public static ShareDeckActivityComponent getShareDeckActivityInjector(final Deck deck) {
        return DaggerShareDeckActivityComponent
                .builder()
                .shareDeckActivityModule(new ShareDeckActivityModule(deck)).build();
    }
}
