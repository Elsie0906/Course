using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class TestClick : MonoBehaviour
{

    public void RestartClick()
    {
        Debug.Log("Button Clicked. restart.");
        GameManager.instance.RestartGame();
    }

    public void HomeClick()
    {
        Debug.Log("Button Clicked. Go home.");
        GameManager.instance.GoStartMenu();
    }

    public void MenuOpenClick()
    {
        Debug.Log("Button Clicked. Set menu.");
        GameManager.instance.GoSetMenu();
    }

    public void MenuCloseClick()
    {
        Debug.Log("Button Clicked. Close set menu.");
        GameManager.instance.CloseSetMenu();
    }
}