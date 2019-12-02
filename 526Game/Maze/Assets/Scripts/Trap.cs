using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Trap : MonoBehaviour
{
    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {

    }

    void OnCollisionEnter(Collision collision)
    {
        print(collision.collider.name);
        Collider collider = collision.collider;
        if (collider.tag == "Player")
        {
            collider.gameObject.GetComponent<PlayerController>().loseHP(10);
        }
    }

    void OnCollisionStay(Collision collision) {
        print(collision.collider.name);
        Collider collider = collision.collider;
        if (collider.tag == "Player")
        {
            print("loseHp");
            collider.gameObject.GetComponent<PlayerController>().loseHP(10);
        }        
    }

    void OnTriggerEnter(Collider collider)
    {
        if (collider.tag == "Player")
        {
            collider.gameObject.GetComponent<PlayerController>().loseHP(10);
        }
    }
}
