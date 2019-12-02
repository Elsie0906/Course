using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class Attack : MonoBehaviour
{
    public float timeCount;
    public float timeCD;
    public Image image;
    public Text text;
    public Button btn;
    public bool isCooling;
    public PlayerController playerController;
    // Start is called before the first frame update
    void Start()
    {
        image.gameObject.SetActive(false);
        text.gameObject.SetActive(false);
        // btn = GetComponent<Button>();
        // playerController = GameObject.Find("Player").GetComponent<PlayerController>();
        // btn.onClick.AddListener(OnClick);
    }
    public void BtnEvent()
    {
        if (!isCooling)
        {
            image.fillAmount = 1;
            image.gameObject.SetActive(true);
            text.gameObject.SetActive(true);
            text.text = timeCD.ToString("f1");
            isCooling = true;
            timeCount = timeCD;
            playerController.triggerAttack();
        }
    }
    // Update is called once per frame
    void Update()
    {
        if (isCooling)
        {
            timeCount -= Time.deltaTime;
            image.fillAmount = timeCount / timeCD;
            text.text = timeCount.ToString("f1");
            if (timeCount <= 0)
            {
                isCooling = false;
                image.gameObject.SetActive(false);
                text.gameObject.SetActive(false);

            }

        }
    }
}
