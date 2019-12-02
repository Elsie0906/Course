using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.SceneManagement;
using UnityEngine.EventSystems;

// Development List
// Attack Button Improvement
// Enemy Creation

public enum ActionState
{
    Idle,
    Walk,
    Attack,
    Die,
}
// TODO:
// 解决人物漂浮问题

public class PlayerController : MonoBehaviour
{
    CharacterController characterController;
    Animator animator;
    public float walkSpeed = 6.0F;
    public const float ATTACK_RATE = 2.0f;
    public const float ATTACK_RANGE = 2.0f;
    // public float jumpSpeed = 8.0F;
    // public float gravity = 20.0F;
    public VariableJoystick joystick;
    public int dir = 0;
    public ActionState state;
    Vector3 cameraOffset;
    Camera camera;
    Vector3 movement;
    public int hp = 100;
    public int coldHp = 100;

    HealthBarControl hpControl;
    ManaBarControl coldControl;
    FrostEffect frostEffect;
    public AudioClip[] audios;

    void Start()
    {                 
        characterController = GetComponent<CharacterController>();
        animator = GetComponent<Animator>();
        joystick = GameObject.Find("Variable Joystick").GetComponent<VariableJoystick>();
        state = ActionState.Idle;
        camera = GameObject.Find("FirstPersonView").GetComponent<Camera>();
        this.cameraOffset = Camera.main.transform.position - this.transform.position;
        hpControl = (HealthBarControl)GameObject.FindGameObjectWithTag("HPBar").GetComponent(typeof(HealthBarControl));
        coldControl = (ManaBarControl)GameObject.FindGameObjectWithTag("ManaBar").GetComponent(typeof(ManaBarControl));
        frostEffect = GameObject.Find("FirstPersonView").GetComponent<FrostEffect>();

        if (GameManager.instance != null)
        {
            this.hp = GameManager.instance.hp;
            this.coldHp = GameManager.instance.coldHp;

            hpControl.setValue(this.hp);
            coldControl.setValue(this.coldHp);

            this.transform.position = GameManager.instance.position;
            this.transform.rotation = GameManager.instance.rotation;
        }

        Debug.Log("Start for Player: hp " + this.hp + ", coldHp: " + this.coldHp + ", position: "+ this.transform.position 
                    + "instance position: " + GameManager.instance.position);
    }

    void Update()
    {
        float dx = 0;
        float dy = 0;

        // if (!EventSystem.current.IsPointerOverGameObject()) {
                for (int i = 0; i < Input.touchCount; i++)
                {
                    if (Input.touches[i].position.x > 600)
                    {
                        print(Input.touchCount);
                        if (Input.GetTouch(i).phase == TouchPhase.Moved)
                        {
                            Vector3 pos = Input.GetTouch(i).deltaPosition;
                            dx = pos.x * 10;
                            dy = pos.y * 10;
                        }
                    }
                }
        // }

        if (Input.GetMouseButton(1))
        {
            dx = Input.GetAxis("Mouse X") * 100;
            dy = Input.GetAxis("Mouse Y") * 100;
        }

        Vector2 targetAngles = transform.eulerAngles;
        targetAngles.x -= dy;
        targetAngles.y += dx;
        transform.rotation = Quaternion.Euler(Vector2.Lerp(transform.eulerAngles, targetAngles, Time.deltaTime));
        // transform.localEulerAngles += new Vector3(0, dx * Time.deltaTime);

    }

    void FixedUpdate() {
        // TODO: Do we really need ActionState?
        movement = Vector3.forward * joystick.Vertical + Vector3.right * joystick.Horizontal;
        switch (state)
        {
            case (ActionState.Idle):
                animator.SetBool("isWalk", false);
                check();
                break;
            case (ActionState.Walk):
                move();
                break;
            case (ActionState.Attack):
                attack();
                break;
        }
    }

    void check() {
        if (movement != Vector3.zero) {
            this.state = ActionState.Walk;
        } else {
            this.state = ActionState.Idle;
        }
    }
    void move() {
        float rotateY = 0f;
        if (joystick.Vertical > 0) {
            rotateY = Mathf.Atan(joystick.Horizontal / joystick.Vertical) * 180 / Mathf.PI;
        }else if (joystick.Vertical < 0 && joystick.Horizontal < 0)
        {
            rotateY = -180 + Mathf.Atan(joystick.Horizontal / joystick.Vertical) * 180 / Mathf.PI;
        }
        else if (joystick.Vertical < 0 && joystick.Horizontal > 0)
        {
            rotateY = 180 + Mathf.Atan(joystick.Horizontal / joystick.Vertical) * 180 / Mathf.PI;
        }
        this.transform.Rotate(0, rotateY * 3 / 180, 0);
        characterController.SimpleMove(this.transform.forward * 5);
        animator.SetBool("isWalk", true);

        // Vector3 moveDirection = new Vector3(ETCInput.GetAxis("Horizontal"), 0.0f, ETCInput.GetAxis("Vertical"));
        // moveDirection = transform.TransformDirection(moveDirection);
        // characterController.Move(moveDirection * walkSpeed * Time.deltaTime);

        Camera.main.transform.position = transform.position + this.cameraOffset;
        check();
    }

    public void resetHP()
    {
        this.hp = 100;

        if (GameManager.instance != null)
        {
            GameManager.instance.hp = 100;
        }

        hpControl.setValue(this.hp);
    }

    public void loseHP(int toLose)
    {
        //Debug.Log("loseHP: " + this.hp);
        this.hp -= toLose;
        //print(this.hp);
        this.hp = this.hp < 0 ? 0 : this.hp;
        hpControl.setValue(this.hp);

        if (GameManager.instance != null)
        {
            GameManager.instance.hp = this.hp;
        }

        // need to do failure check
        if (this.hp <= 0)
        {
            this.GetComponent<AudioSource>().clip = audios[3];
            this.GetComponent<AudioSource>().Play();
            GameManager.instance.LoseGame();
        } else {
            print("hello");
            this.GetComponent<AudioSource>().clip = audios[4];
            this.GetComponent<AudioSource>().Play();
        }
    }

    public void resetColdHP()
    {
        this.coldHp = 100;

        if (GameManager.instance != null)
        {
            GameManager.instance.coldHp = 100;
        }

        coldControl.setValue(this.coldHp);
        int stage = 9 - (this.coldHp / 10);
        frostEffect.setFrostAmountToStage(stage);
    }

    public void loseColdHP(int toLose)
    {
        Debug.Log("loseColdHP: " + this.coldHp);
        this.coldHp -= toLose;
        // print(this.coldHp);

        this.coldHp = this.coldHp < 0 ? 0 : this.coldHp;
        coldControl.setValue(this.coldHp);

        if (GameManager.instance != null)
        {
            GameManager.instance.coldHp = this.coldHp;
        }

        int stage = 9 - (this.coldHp / 10);
        frostEffect.setFrostAmountToStage(stage);

        // need to do failure check
        if (this.coldHp <= 0)
        {
            this.GetComponent<AudioSource>().clip = audios[3];
            this.GetComponent<AudioSource>().Play();
            GameManager.instance.LoseGame();
        }
    }

    private void attack() {
        Collider[] nearByObject = Physics.OverlapSphere(transform.position, ATTACK_RANGE);
        animator.SetTrigger("attack");
        Random rand = new Random();
        this.GetComponent<AudioSource>().clip = audios[Random.Range(0, 3)];
        this.GetComponent<AudioSource>().Play();
        foreach (Collider obj in nearByObject)
        {
            if ("Zombie" == obj.gameObject.tag)
            {
                obj.GetComponent<EnemyAI>().loseHP(30);
                // break; // only attack one zombie
            }
        }
        check();
    }

    public void triggerAttack() {
        this.state = ActionState.Attack;
    }
}
