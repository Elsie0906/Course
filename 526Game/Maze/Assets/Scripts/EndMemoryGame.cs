using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;

public class EndMemoryGame : MonoBehaviour
{
    public void Clicked()
    {
        Debug.Log("Button Clicked. end Memory Game.");
        string goToScene = "SampleScene";
        SceneManager.LoadScene(goToScene);
    }
}
