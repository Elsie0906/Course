using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;

public class MemoryGameManager : MonoBehaviour
{
    public Sprite[] cardFace;
    public Sprite cardBack;
    public GameObject[] cards;

    private bool init = false;
    private int matches = 6;

    // Update is called once per frame
    void Update()
    {
        if (!init)
        {
            initializeCards();
        }
        if (Input.GetMouseButtonUp(0))
        {
            checkCards();
        }
    }

    void initializeCards()
    {
        for(int id = 0; id<2; id++)
        {
            for(int i=1; i<=6; i++)
            {
                bool test = false;
                int choice = 0;
                while (!test)
                {
                    choice = Random.Range(0, cards.Length);
                    test = !(cards[choice].GetComponent<Card>().Initialized);
                }
                cards[choice].GetComponent<Card>().CardValue = i;
                cards[choice].GetComponent<Card>().Initialized = true;
            }
        }

        foreach (GameObject c in cards)
        {
            c.GetComponent<Card>().setupGraphics();
        }
        if (!init)
        {
            init = true;
        }
    }

    public Sprite getCardBack()
    {
        return cardBack;
    }
    public Sprite getCardFace(int i)
    {
        return cardFace[i-1];
    }

    void checkCards()
    {
        List<int> c = new List<int>();
        for(int i=0; i<cards.Length; i++)
        {
            if(cards[i].GetComponent<Card>().State == 1)
            {
                c.Add(i);
            }
        }
        if(c.Count == 2)
        {
            cardComparison(c);
        }
    }

    void cardComparison(List<int> c)
    {
        Card.DO_NOT_FLIP = true;
        int x = 0;
        
        if(cards[c[0]].GetComponent<Card>().CardValue == cards[c[1]].GetComponent<Card>().CardValue)
        {
            x = 2;
            matches--;
            if(matches == 0)
            {
                //success
                if (GameManager.instance != null)
                {
                    GameManager.instance.finish = true; // exit controller opened
                }
                SceneManager.LoadScene("SampleScene");
            }
        }
        
        for(int i=0; i<c.Count; i++)
        {
            cards[c[i]].GetComponent<Card>().State = x;
            cards[c[i]].GetComponent<Card>().falseCheck();
        }
    }
}
