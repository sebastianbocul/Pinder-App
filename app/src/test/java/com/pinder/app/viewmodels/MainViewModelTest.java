package com.pinder.app.viewmodels;

import android.app.Application;

import com.pinder.app.InstantExecutorExtension;
import com.pinder.app.LiveDataTestUtil;
import com.pinder.app.models.Card;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(InstantExecutorExtension.class)
public class MainViewModelTest {
    //system under test
    private MainViewModel mainViewModel;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        Application application = new Application();
        mainViewModel=new MainViewModel(application);
    }


    @Test
    void removeFirstObjectInAdapter_returnTrue() throws Exception {
        //Arrange
        Map<Object, Object> tagsMap = new HashMap<>();
        tagsMap.put("default", true);
        tagsMap.put("date", true);
        Card card1 = new Card("userId1","Ola","url:dsdsadsdsa","#default #date",tagsMap,100,true);
        Card card2 = new Card("userId2","Ala","url:dsdsadsdsa","#default #date",tagsMap,100,true);
        Card card3 = new Card("userId3","Mola","url:dsdsadsdsa","#default #date",tagsMap,100,true);
        LiveDataTestUtil<ArrayList<Card>> liveDataTestUtil = new LiveDataTestUtil<>();
        ArrayList<Card> rowItems = new ArrayList<>();
        ArrayList<Card> cards = new ArrayList<>();
        rowItems.addAll(Arrays.asList(card1,card2,card3));
        cards.addAll(rowItems);
        cards.remove(0);
        //Act
        mainViewModel.rowItemsLD.setValue(rowItems);
        mainViewModel.removeFirstObjectInAdapter();
        rowItems=mainViewModel.rowItemsLD.getValue();
        //Assert
        Assertions.assertTrue(cards.equals(rowItems));
        System.out.println("Row items:" +rowItems.toString());
    }

    @Test
    void removeTwoFirstObjectsInAdapter_returnTrue() throws Exception {
        //Arrange
        Map<Object, Object> tagsMap = new HashMap<>();
        tagsMap.put("default", true);
        tagsMap.put("date", true);
        Card card1 = new Card("userId1","Ola","url:dsdsadsdsa","#default #date",tagsMap,100,true);
        Card card2 = new Card("userId2","Ala","url:dsdsadsdsa","#default #date",tagsMap,100,true);
        Card card3 = new Card("userId3","Mola","url:dsdsadsdsa","#default #date",tagsMap,100,true);
        LiveDataTestUtil<ArrayList<Card>> liveDataTestUtil = new LiveDataTestUtil<>();
        ArrayList<Card> rowItems = new ArrayList<>();
        ArrayList<Card> cards = new ArrayList<>();
        rowItems.addAll(Arrays.asList(card1,card2,card3));
        cards.addAll(rowItems);
        cards.remove(0);
        cards.remove(0);
        //Act
        mainViewModel.rowItemsLD.setValue(rowItems);
        mainViewModel.removeFirstObjectInAdapter();
        mainViewModel.removeFirstObjectInAdapter();
        rowItems=mainViewModel.rowItemsLD.getValue();
        //Assert
        Assertions.assertTrue(cards.equals(rowItems));
        System.out.println("Row items:" +rowItems.toString());
    }

    @Test
    void removeThreeFirstObjectsInAdapter_returnTrue() throws Exception {
        //Arrange
        Map<Object, Object> tagsMap = new HashMap<>();
        tagsMap.put("default", true);
        tagsMap.put("date", true);
        Card card1 = new Card("userId1","Ola","url:dsdsadsdsa","#default #date",tagsMap,100,true);
        Card card2 = new Card("userId2","Ala","url:dsdsadsdsa","#default #date",tagsMap,100,true);
        Card card3 = new Card("userId3","Mola","url:dsdsadsdsa","#default #date",tagsMap,100,true);
        LiveDataTestUtil<ArrayList<Card>> liveDataTestUtil = new LiveDataTestUtil<>();
        ArrayList<Card> rowItems = new ArrayList<>();
        ArrayList<Card> cards = new ArrayList<>();
        rowItems.addAll(Arrays.asList(card1,card2,card3));
        cards.addAll(rowItems);
        cards.remove(0);
        cards.remove(0);
        cards.remove(0);
        //Act
        mainViewModel.rowItemsLD.setValue(rowItems);
        mainViewModel.removeFirstObjectInAdapter();
        mainViewModel.removeFirstObjectInAdapter();
        mainViewModel.removeFirstObjectInAdapter();
        rowItems=mainViewModel.rowItemsLD.getValue();
        //Assert
        Assertions.assertTrue(cards.equals(rowItems));
        System.out.println("Row items:" +rowItems.toString());
    }

    @Test
    void tryToRemoveEmptyListOfCards_returnTrue() throws Exception {
        //Arrange
        Map<Object, Object> tagsMap = new HashMap<>();
        tagsMap.put("default", true);
        tagsMap.put("date", true);
        Card card1 = new Card("userId1","Ola","url:dsdsadsdsa","#default #date",tagsMap,100,true);
        Card card2 = new Card("userId2","Ala","url:dsdsadsdsa","#default #date",tagsMap,100,true);
        Card card3 = new Card("userId3","Mola","url:dsdsadsdsa","#default #date",tagsMap,100,true);
        LiveDataTestUtil<ArrayList<Card>> liveDataTestUtil = new LiveDataTestUtil<>();
        ArrayList<Card> rowItems = new ArrayList<>();
        ArrayList<Card> cards = new ArrayList<>();
        rowItems.addAll(Arrays.asList(card1,card2,card3));
        cards.addAll(rowItems);
        cards.remove(0);
        cards.remove(0);
        cards.remove(0);
        //Act
        mainViewModel.rowItemsLD.setValue(rowItems);
        mainViewModel.removeFirstObjectInAdapter();
        mainViewModel.removeFirstObjectInAdapter();
        mainViewModel.removeFirstObjectInAdapter();
        mainViewModel.removeFirstObjectInAdapter();
        mainViewModel.removeFirstObjectInAdapter();
        mainViewModel.removeFirstObjectInAdapter();
        mainViewModel.removeFirstObjectInAdapter();
        rowItems=mainViewModel.rowItemsLD.getValue();
        //Assert
        Assertions.assertTrue(cards.equals(rowItems));
        System.out.println("Row items:" +rowItems.toString());
    }



}
