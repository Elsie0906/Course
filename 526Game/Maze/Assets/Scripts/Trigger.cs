using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Trigger : MonoBehaviour
{
	private Door m_Door;
    private bool enterController = false;
    private int counter;
    // Start is called before the first frame update
    void Start()
    {
        m_Door = GameObject.Find("DoorShaft1").GetComponent<Door>();
    }
    void OnTriggerEnter(Collider collider)
    {
        if (GameManager.instance != null && GameManager.instance.finish == true)
        {
            Debug.Log("enter: " + collider.name);
            m_Door.OpenDoorMethod();
        }

    }

    void OnTriggerExit(Collider collider)
    {
        if(GameManager.instance != null && GameManager.instance.finish == true)
        {
            Debug.Log("exit");
            GameManager.instance.GoWinMessage();
            m_Door.CloseDoorMethod();
        }
    }
    // Update is called once per frame
    void Update()
    {
        //Debug.Log("Update: " + enterCollide);

        //if (enterCollide)
        //{
        //    //Debug.Log("Update: " + m_Door.GetIsOpen());

        //    if (Input.GetKeyUp(KeyCode.F))
        //    {
        //        if (m_Door.GetIsOpen())
        //        {
        //            m_Door.CloseDoorMethod();

        //        }
        //        else
        //        {
        //            m_Door.OpenDoorMethod();
        //        }
        //    }
        //}
    }
}
