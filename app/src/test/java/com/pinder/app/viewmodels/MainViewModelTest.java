package com.pinder.app.viewmodels;

import android.app.Application;

import com.pinder.app.InstantExecutorExtension;
import com.pinder.app.LiveDataTestUtil;
import com.pinder.app.models.Card;
import com.pinder.app.repository.MainRepository;
import com.pinder.app.util.Resource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(InstantExecutorExtension.class)
public class MainViewModelTest {
    //system under test
    private MainViewModel mainViewModel;

    @BeforeEach
    public void init() {
        MainRepository mainRepository=null;
        MockitoAnnotations.initMocks(this);
        Application application = new Application();
        mainViewModel=new MainViewModel(mainRepository);
        System.out.println("DUpa");
    }

    HashMap<Object, Object> tagsMap = new HashMap<Object, Object>(){
        {
            put("default", true);
            put("date", true);
        }
    };
    ArrayList<String> urls = new ArrayList<>(Arrays.asList("url:saddaasd","url2:sdajdajs"));
    Card card1 = new Card("userId1","Ola","url:dsdsadsdsa",urls,"Female","02/04-2000","#default #date",tagsMap,100,"location",true,"mydescription");
    Card card2 = new Card("userId2","Ala","url:dsdsadsdsa",urls,"Female","02/04-2000","#default #date",tagsMap,100,"location",true,"mydescription");
    Card card3 = new Card("userId3","Mola","url:dsdsadsdsa",urls,"Female","02/04-2000","#default #date",tagsMap,100,"location",true,"mydescription");

    @Test
    void removeFirstObjectInAdapter_returnTrue() throws Exception {
        //Arrange
        LiveDataTestUtil<ArrayList<Card>> liveDataTestUtil = new LiveDataTestUtil<>();
        ArrayList<Card> cardsArray = new ArrayList<>();
        ArrayList<Card> cards = new ArrayList<>();
        cardsArray.addAll(Arrays.asList(card1,card2,card3));
        cards.addAll(cardsArray);
        cards.remove(0);
        //Act
        mainViewModel.cardsArrayLD.setValue(Resource.success(cardsArray));
        mainViewModel.removeFirstObjectInAdapter();
        cardsArray=mainViewModel.cardsArrayLD.getValue().data;
        //Assert
        Assertions.assertTrue(cards.equals(cardsArray));
        System.out.println("Row items:" +cardsArray.toString());
    }

    @Test
    void removeTwoFirstObjectsInAdapter_returnTrue() throws Exception {
        //Arrange
        LiveDataTestUtil<ArrayList<Card>> liveDataTestUtil = new LiveDataTestUtil<>();
        ArrayList<Card> cardsArray = new ArrayList<>();
        ArrayList<Card> cards = new ArrayList<>();
        cardsArray.addAll(Arrays.asList(card1,card2,card3));
        cards.addAll(cardsArray);
        cards.remove(0);
        cards.remove(0);
        //Act
        mainViewModel.cardsArrayLD.setValue(Resource.success(cardsArray));
        mainViewModel.removeFirstObjectInAdapter();
        mainViewModel.removeFirstObjectInAdapter();
        cardsArray=mainViewModel.cardsArrayLD.getValue().data;
        //Assert
        Assertions.assertTrue(cards.equals(cardsArray));
        System.out.println("Row items:" +cardsArray.toString());
    }

    @Test
    void removeThreeFirstObjectsInAdapter_returnTrue() throws Exception {
        //Arrange
        LiveDataTestUtil<ArrayList<Card>> liveDataTestUtil = new LiveDataTestUtil<>();
        ArrayList<Card> cardsArray = new ArrayList<>();
        ArrayList<Card> cards = new ArrayList<>();
        cardsArray.addAll(Arrays.asList(card1,card2,card3));
        cards.addAll(cardsArray);
        cards.remove(0);
        cards.remove(0);
        cards.remove(0);
        //Act
        mainViewModel.cardsArrayLD.setValue(Resource.success(cardsArray));
        mainViewModel.removeFirstObjectInAdapter();
        mainViewModel.removeFirstObjectInAdapter();
        mainViewModel.removeFirstObjectInAdapter();
        cardsArray=mainViewModel.cardsArrayLD.getValue().data;
        //Assert
        Assertions.assertTrue(cards.equals(cardsArray));
        System.out.println("Row items:" +cardsArray.toString());
    }

    @Test
    void tryToRemoveEmptyListOfCards_returnTrue() throws Exception {
        //Arrange
        LiveDataTestUtil<ArrayList<Card>> liveDataTestUtil = new LiveDataTestUtil<>();
        ArrayList<Card> cardsArray = new ArrayList<>();
        ArrayList<Card> cards = new ArrayList<>();
        cardsArray.addAll(Arrays.asList(card1,card2,card3));
        cards.addAll(cardsArray);
        cards.remove(0);
        cards.remove(0);
        cards.remove(0);
        //Act
        mainViewModel.cardsArrayLD.setValue(Resource.success(cardsArray));
        mainViewModel.removeFirstObjectInAdapter();
        mainViewModel.removeFirstObjectInAdapter();
        mainViewModel.removeFirstObjectInAdapter();
        mainViewModel.removeFirstObjectInAdapter();
        mainViewModel.removeFirstObjectInAdapter();
        mainViewModel.removeFirstObjectInAdapter();
        mainViewModel.removeFirstObjectInAdapter();
        cardsArray=mainViewModel.cardsArrayLD.getValue().data;
        //Assert
        Assertions.assertTrue(cards.equals(cardsArray));
        System.out.println("Row items:" +cardsArray.toString());
    }



}
