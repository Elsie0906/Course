using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;

public class StartMemoryGame : MonoBehaviour
{

    public void OnMouseDown()
    {
       
        Debug.Log("checkStatus: "+ GameManager.instance.numOfTriggeredBonfires());
        if(GameManager.instance.numOfTriggeredBonfires() == 10)
        {
            Debug.Log("Button Clicked. start Memory Game.");
            string goToScene = "MemoryGame";
            GameManager.instance.BreakGame();
            SceneManager.LoadScene(goToScene);
        }
        else
        {
            GameManager.instance.GoCheckMessage();
        }
    }

   
}
