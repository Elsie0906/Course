using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;

public class StartMenu : MonoBehaviour
{
    void Start()
    {
        if (GameManager.instance != null)
        {
            Destroy(GameObject.Find("GameManager"));
        }
    }
    public void Clicked()
    {
        SceneManager.LoadScene("SampleScene");
    }
}
