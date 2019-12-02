using UnityEngine;
using System.Collections;
using UnityEngine.SceneManagement;
using UnityEngine.UI;

public enum GameState
{
    RUN,
    LOSE,
    WIN
}

public class GameManager : MonoBehaviour
{
    public static GameManager instance = null;
    public static GameState MyState;

    public GameObject win;
    public GameObject lose;
    public GameObject setMenus;
    public GameObject winMessage;
    public GameObject checkMessage;


    //GameObject player = null;

    public int hp;
    public int coldHp;

    public Vector3 position;
    public Quaternion rotation;

    //public BonFireTrigger[] bonfires;
    public bool[] bonfireStates;

    public bool finish;

    // Use this for initialization
    void Awake()
    {

        // singleton: only one instance/GameManager, which is used for storing global variables

        if (instance == null)
        {
            Debug.Log("Awake: initial, hp: " + this.hp + ", coldHp: " + this.coldHp);
            // setup initial value
            instance = this;
            instance.hp = 100;
            instance.coldHp = 100;
            instance.position = new Vector3(-123.3f, 0f, 39.65f);
            instance.rotation = Quaternion.Euler(0f,0f,0f);
            instance.bonfireStates = new bool[10];
            instance.finish = false;
            DontDestroyOnLoad(instance);

        }
        else if (instance != this)
        {
            // when scnene change happened, by default, it would create a new instance
            // destroy the new instance, keep the old one
            recoverBonfires();
            Debug.Log("Number of triggered bonfires after recovery: " + numOfTriggeredBonfires());

            Debug.Log("Awake: destroy object, hp: " + instance.hp + ", this.hp: " + this.hp);
            Destroy(this.gameObject);

        }
        instance.lose = GameObject.Find("lose");
        instance.setMenus = GameObject.Find("setMenu");
        instance.winMessage = GameObject.Find("winMessage");
        instance.checkMessage = GameObject.Find("checkMessage");
        InitGame();
    }


    void InitGame()
    {
        //instance.lose = GameObject.Find("lose");
        instance.lose.SetActive(false);


        //instance.setMenus = GameObject.Find("setMenu");
        instance.setMenus.SetActive(false);
        instance.winMessage.SetActive(false);
        instance.checkMessage.SetActive(false);

        Debug.Log("InitGame(),hp: " + instance.hp + ",coldHp: " + instance.coldHp);
    }

    public void BreakGame()
    {
        HealthBarControl hpControl = (HealthBarControl)GameObject.FindGameObjectWithTag("HPBar").GetComponent(typeof(HealthBarControl));
        ManaBarControl coldControl = (ManaBarControl)GameObject.FindGameObjectWithTag("ManaBar").GetComponent(typeof(ManaBarControl));
         
        instance.hp = hpControl.getValue();
        instance.coldHp = coldControl.getValue();
        
        Debug.Log("BreakGame(),hp: " + hp + ",coldHp: " + coldHp);
        //Debug.Log("check instance: " + hpControl.getValue());

        PlayerController player = (PlayerController)GameObject.Find("Player").GetComponent(typeof(PlayerController));

        instance.position = player.transform.position;
        instance.rotation = player.transform.rotation;

        Debug.Log("cur position: " + player.transform.position);

        recordBonfires();
        Debug.Log("Number of triggered bonfires when break: " + numOfTriggeredBonfires());
    }

    public void LoseGame()
    {
        PauseGame();
        if (instance.lose != null)
        {
            instance.lose.SetActive(true);
        }
        else
        {
            Debug.Log("lose is null");
        }
    }

    public void RestartGame()
    {
        Debug.Log("RestartGame()");
        // reset with intial value
        instance.hp = 100;
        instance.coldHp = 100;
        instance.position = new Vector3(-123.3f, 0f, 39.65f);
        instance.rotation = Quaternion.Euler(0f, 0f, 0f);

        InitGame();
        ResumeGame();
        SceneManager.LoadScene("SampleScene");
       
    }

    //// Update is called once per frame
    //void Update()
    //{

    //}
    public void GoStartMenu()
    {
        ResumeGame();
        SceneManager.LoadScene("StartMenu");
    }

    public void GoSetMenu()
    {
        if (instance.setMenus != null)
        {
            PauseGame();
            instance.setMenus.SetActive(true);
        }
        else
        {
            Debug.Log("setMenus is null");
        }
    }

    public void CloseSetMenu()
    {
        if (instance.setMenus != null)
        {
            instance.setMenus.SetActive(false);
            ResumeGame();
        }
        else
        {
            Debug.Log("setMenus is null");
        }
    }


    void PauseGame()
    {
        Debug.Log("PauseGame()");
        Time.timeScale = 0;
    }

    void ResumeGame()
    {
        Debug.Log("ResumeGame()");
        Time.timeScale = 1;
    }

    void recordBonfires()
    {
        for (int i = 0; i < 10; i++)
        {
            string index = "Bonfire" + (i + 1);
            BonFireTrigger cur = (BonFireTrigger)GameObject.Find(index).GetComponent(typeof(BonFireTrigger));
            instance.bonfireStates[i] = cur.State;
        }
    }


    void recoverBonfires()
    {
        for (int i = 0; i < 10; i++)
        {
            string index = "Bonfire" + (i + 1);
            BonFireTrigger cur = (BonFireTrigger)GameObject.Find(index).GetComponent(typeof(BonFireTrigger));
            cur.State = instance.bonfireStates[i];
        }
    }

    //return the number of bonfires that are not triggered
    public int numOfTriggeredBonfires()
    {
        int count = 0;
        for (int i = 0; i < 10; i++)
        {
            string index = "Bonfire" + (i + 1);
            BonFireTrigger cur = (BonFireTrigger)GameObject.Find(index).GetComponent(typeof(BonFireTrigger));
            if (cur.State == true)
            {
                count++;
            }
        }
        return count;
    }

    public void GoWinMessage()
    {
        if (instance.winMessage != null)
        {
            PauseGame();
            instance.winMessage.SetActive(true);
        }
        else
        {
            Debug.Log("winMessage is null");
        }
    }

    public void GoCheckMessage()
    {
        if (instance.checkMessage != null)
        {
            PauseGame();
            GameObject checkMsg = instance.checkMessage.transform.GetChild(0).gameObject;
            Text checkMsgText = checkMsg.GetComponent<Text>();
            checkMsgText.text = (10 - numOfTriggeredBonfires()) + " more bonfires left";
            instance.checkMessage.SetActive(true);
        }
        else
        {
            Debug.Log("checkMessage is null");
        }
    }
    public void CloseCheckMessage()
    {
        if (instance.checkMessage != null)
        {
            instance.checkMessage.SetActive(false);
            ResumeGame();
        }
        else
        {
            Debug.Log("checkMessage is null");
        }
    }
}
