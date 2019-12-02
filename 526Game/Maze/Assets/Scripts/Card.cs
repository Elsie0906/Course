using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class Card : MonoBehaviour
{
    public static bool DO_NOT_FLIP = false;

    [SerializeField]
    private int state;
    [SerializeField]
    private int cardValue;
    [SerializeField]
    private bool initialized = false;

    private Sprite cardBack;
    private Sprite cardFace;

    private GameObject manager;
    // Start is called before the first frame update
    void Start()
    {
        state = 1;
        manager = GameObject.FindGameObjectWithTag("MemoryGameManager");
    }

    // Update is called once per frame
    void Update()
    {
        
    }

    public void setupGraphics()
    {
        cardBack = manager.GetComponent<MemoryGameManager>().getCardBack();
        cardFace = manager.GetComponent<MemoryGameManager>().getCardFace(cardValue);

        flipCard();
    }
    
    public void flipCard()
    {
        if(state == 0)
        {
            state = 1;
        }
        else if(state == 1)
        {
            state = 0;
        }

        if(state == 0 && !DO_NOT_FLIP)
        {
            GetComponent<Image>().sprite = cardBack;
        }
        else if (state == 1 && !DO_NOT_FLIP)
        {
            GetComponent<Image>().sprite = cardFace;
        }
    }

    public int CardValue
    {
        get { return cardValue; }
        set { cardValue = value; }
    }

    public int State
    {
        get { return state; }
        set { state = value; }
    }

    public bool Initialized
    {
        get { return initialized; }
        set { initialized = value; }
    }

    public void falseCheck()
    {
        StartCoroutine(pause());
    }
    IEnumerator pause()
    {
           
        if(state == 0)
        {
            yield return new WaitForSeconds(0.5f);
            GetComponent<Image>().sprite = cardBack;
        }
        else if(state == 1)
        {
            GetComponent<Image>().sprite = cardFace;
        }
        DO_NOT_FLIP = false;
    }
}
