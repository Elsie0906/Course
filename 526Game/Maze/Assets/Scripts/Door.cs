using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Door : MonoBehaviour
{
    Transform tf;
    private bool isOpen = false; 	
    // Start is called before the first frame update
    void Start()
    {
    	tf = gameObject.GetComponent<Transform>();       
    }

    // Update is called once per frame
    void Update()
    {
        
    }
    public void OpenDoorMethod()
    {
        tf.Rotate(Vector3.up, 90);
        Debug.Log("open");
        isOpen = true;

    }
    public void CloseDoorMethod()
    {
        tf.Rotate(Vector3.up, -90);
        Debug.Log("close");
        isOpen = false;
    }

    public bool GetIsOpen()
    {
        return isOpen;
    }
    public void SetIsOpen(bool b)
    {
        isOpen = b;
    }    
}
