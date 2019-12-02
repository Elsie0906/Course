using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class BonFireTrigger : MonoBehaviour
{
    public bool isTriggered;
    PlayerController player;
    CountDownTimer timer;
    // Start is called before the first frame update
    void Start()
    {
        
        player = (PlayerController) GameObject.Find("Player").GetComponent(typeof(PlayerController));
        timer = (CountDownTimer)GameObject.Find("Player").GetComponent(typeof(CountDownTimer));
        if (GameManager.instance != null)
        {
            //Debug.Log("instance is not null");
        }
        else
        {
            isTriggered = false;
        }
    }

    // Update is called once per frame
    void Update()
    {
        if (isTriggered)
        {
            triggerBonFire();
        }
    }
    private void OnTriggerEnter(Collider other)
    {
        Debug.Log("Enter");
        timer.stopTimer();
        if (!isTriggered)
        {// if the bonfire is not triggered, trigger it first
            triggerBonFire();
        }
        //reset hp
        player.resetColdHP();

    }
    private void OnTriggerExit(Collider other)
    {
        /*Debug.Log("Exit");*/
        //Once exit, we can start a timer for player to count down and lose hp
        timer.resetTimer();
    }
    public void triggerBonFire()
    {
        GameObject child = gameObject.transform.GetChild(0).gameObject;
        child.SetActive(true); 
        isTriggered = true;
    }

    public bool State
    {
        get { return isTriggered; }
        set { isTriggered = value; }
    }
}
