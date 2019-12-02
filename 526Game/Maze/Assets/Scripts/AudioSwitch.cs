using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class AudioSwitch : MonoBehaviour
{
    public AudioClip[] audios;
    // Use this for initialization
    void Start()
    {
        this.GetComponent<AudioSource>().clip = audios[1];
        this.GetComponent<AudioSource>().Play();
    }

    // Update is called once per frame
    void Update()
    {
        this.GetComponent<AudioSource>().clip = audios[0];
        this.GetComponent<AudioSource>().Play();
    }
}
